package com.assist4j.sequence.dao;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.assist4j.sequence.bean.SequenceHolder;
import com.assist4j.sequence.dao.loadbalancer.IRule;
import com.assist4j.sequence.dao.loadbalancer.RoundRobinRule;
import com.assist4j.sequence.exception.SequenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


/**
 * @author yuwei
 */
public abstract class AbstractGroupSequenceDao extends AbstractSequenceDao {
	private static final Logger log = LoggerFactory.getLogger(AbstractGroupSequenceDao.class);
	private Map<Integer, AtomicInteger> excludedSegment;
	/**
	 * 一旦被剔除，最多被忽略的次数
	 */
	private int maxSkipCount = 10;
	/**
	 * 同步等待的最大时长(毫秒)
	 */
	private long maxWaitMillis = 3000L;
	private ExecutorService threadPool;
	private IRule rule;
	private String ruleClassName;


	public AbstractGroupSequenceDao() {
		this.excludedSegment = new ConcurrentHashMap<Integer, AtomicInteger>();
		this.threadPool = Executors.newFixedThreadPool(1);
	}
	
	@Override
	public void init() {
		initDataSourceRouteRule();
		super.init();
	}
	
	private void initDataSourceRouteRule() {
		if(rule != null) {
			return;
		}
		
		int segCount = getSegmentCount();
		
		if(ruleClassName == null || "".equals(ruleClassName.trim())) {
			rule = new RoundRobinRule(segCount);
		} else {
			try {
				Class<?> ruleClass = Class.forName(ruleClassName);
				if(!IRule.class.isAssignableFrom(ruleClass)) {
					throw new SequenceException("[ruleClassName] must be a subclass of [" + IRule.class.getName() + "], but now it is [" + ruleClassName + "].");
				}
				rule = (IRule)ruleClass.newInstance();
				rule.setSegmentCount(segCount);
			} catch (Exception e) {
				throw new SequenceException(e);
			}
		}
	}

	@Override
	public void ensure(String seqName) {
		for(int i = 0; i < getSegmentCount(); ++i) {
			Long oldValue = selectSeqValue(i, seqName);
			if (oldValue == null) {
				insertSeq(i, seqName, i * getInnerStep());
			} else {
				long oldValue0 = adjustCurrentValue(i, oldValue);
				if(oldValue != oldValue0) {
					updateSeqValue(i, seqName, oldValue, oldValue0);
				}
			}
		}
	}

	@Override
	public SequenceHolder nextRange(final String seqName) {
		Assert.notNull(seqName, "序列名称不能为空");
		int segCount = getSegmentCount();
		
		int retryTimes = getRetryTimes();
		for(int i = 0; i < retryTimes + 1; ++i) {
			int segment = rule.chooseSegment();
			Long oldValue = selectSeqValueFromASegment(segment, seqName);
			
			if (oldValue == null || oldValue < 0L || oldValue > Long.MAX_VALUE - 100000000L) {
				log.error("Can not get sequence, segment = {}, seqName = {}, value = {}.", segment, seqName, oldValue);
				continue;
			}
			
			long adjustOldValue = adjustCurrentValue(segment, oldValue);
			long newValue = adjustOldValue + (long)(segCount * getInnerStep());
			try {
				updateSeqValue(segment, seqName, oldValue, newValue);
			} catch(Exception e) {
				log.error("", e);
				continue;
			}
			
			if(i >= retryTimes - 1) {
				cleanExcludedSegment();
			}
			
			return new SequenceHolder(adjustOldValue + 1L, adjustOldValue + (long)getInnerStep());
		}
		throw new SequenceException("Retried too many times, retryTimes = " + retryTimes);
	}
	
	protected void cleanExcludedSegment() {
		if(!CollectionUtils.isEmpty(excludedSegment)) {
			excludedSegment.clear();
		}
	}
	
	private Long selectSeqValueFromASegment(int segment, String seqName) {
		int segCount = getSegmentCount();
		
		if (excludedSegment.get(segment) != null) {
			if (excludedSegment.get(segment).incrementAndGet() <= maxSkipCount) {
				return null;
			}

			excludedSegment.remove(segment);
			log.info("{}次数已过，index为{}的数据源后续重新尝试取序列", maxSkipCount, segment);
		}

		if (excludedSegment.size() >= segCount - 1) {
			return selectSeqValue(segment, seqName);
		} else {
			try {
				Future<Long> future = threadPool.submit(() -> selectSeqValue(segment, seqName));
				return future.get(maxWaitMillis, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				log.error("", e);
				if (excludedSegment.size() < segCount - 1) {
					excludedSegment.put(segment, new AtomicInteger(0));
					log.error("暂时剔除index为{}的数据源，{}次后重新尝试", segment, maxSkipCount);
				}
				return null;
			}
		}
	}

	private long adjustCurrentValue(int segment, long currentValue) {
		if(currentValue < 0) {
			currentValue = 0;
		}
		
		int outStep = getSegmentCount() * getInnerStep();
		long standardValue = currentValue - currentValue % (long)outStep + (long)(segment * getInnerStep());
		return currentValue <= standardValue ? standardValue : (long)(standardValue + outStep);
	}
	
	protected abstract int getSegmentCount();

	public void setMaxSkipCount(int maxSkipCount) {
		Assert.isTrue(maxSkipCount > 0, "Property maxSkipCount must be larger than zero, maxSkipCount = " + maxSkipCount);
		this.maxSkipCount = maxSkipCount;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		Assert.isTrue(maxWaitMillis > 0, "Property maxWaitMillis must be larger than zero, maxWaitMillis = " + maxWaitMillis);
		this.maxWaitMillis = maxWaitMillis;
	}

	public void setRuleClassName(String ruleClassName) {
		this.ruleClassName = ruleClassName;
	}
}
