package com.assist4j.session.conf;


import com.assist4j.session.SessionConstant;
import com.assist4j.session.cache.SessionCache;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author yuwei
 */
public class SessionConf {
	private PathPattern exclusivePattern;
	private ValueSplit valueSplit;
	/**
	 * session失效时间(分钟)
	 */
	private int maxInactiveInterval;
	private String applicationName;
	private SessionCache cache;


	private SessionConf() {

	}
	private static class Holder {
		private static final SessionConf instance = new SessionConf();
	}

	public static SessionConf getInstance() {
		return Holder.instance;
	}

	public void setExclusivePattern(String[] exclusiveURLs) {
		exclusivePattern = new PathPattern(exclusiveURLs);
	}
	public PathPattern getExclusivePattern() {
		return exclusivePattern;
	}

	public void setValueSplit(ValueSplit valueSplit) {
		this.valueSplit = valueSplit;
	}
	public ValueSplit getValueSplit() {
		return valueSplit;
	}

	public void setCache(SessionCache cache) {
		Assert.notNull(cache, "[cache] is required.");
		this.cache = cache;
		setMaxInactiveInterval(cache.getMaxInactiveInterval());
		setApplicationName(cache.getApplicationName());
	}
	public SessionCache getCache() {
		Assert.notNull(cache, "[cache] is required.");
		return cache;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		if (maxInactiveInterval <= 0) {
			this.maxInactiveInterval = SessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL;
		} else {
			this.maxInactiveInterval = maxInactiveInterval;
		}
	}
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setApplicationName(String applicationName) {
		Assert.notNull(applicationName, "[applicationName] is required.");
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher m = Pattern.compile(regEx).matcher(applicationName);
		this.applicationName = m.replaceAll("").trim();
	}
	public String getApplicationName() {
		Assert.notNull(applicationName, "[applicationName] is required.");
		return applicationName;
	}
}
