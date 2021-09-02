package com.yuweix.assist4j.schedule.base;


import com.yuweix.assist4j.schedule.LeaderElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yuwei
 */
public abstract class AbstractTask {
	private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);
	private static final LeaderElector DEFAULT_LEADER_ELECTOR = new LeaderElector() {
		@Override
		public String acquire(String lock) {
			return getLocalNode();
		}
		@Override
		public void release(String lock) {
			//NO-OP
		}
		@Override
		public String getLocalNode() {
			return "local";
		}
	};

	private static final Map<Class<? extends AbstractTask>, LeaderElector> ELECTOR_MAP_REF = new ConcurrentHashMap<>();

	public AbstractTask() {

	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		LeaderElector leaderElector = getLeaderElector();
		if (leaderElector == null) {
			leaderElector = DEFAULT_LEADER_ELECTOR;
		}

		String lockName = getLockName();
		boolean release = getRelease();
		String localNode = leaderElector.getLocalNode();
		String leaderNode = leaderElector.acquire(lockName);
		if (localNode != null && localNode.equals(leaderNode)) {
			before();
			executeTask();
			if (release) {
				leaderElector.release(lockName);
			}
			after();
			long timeCost = System.currentTimeMillis() - startTime;
			log.info("Job executed here, JobName: {}, LocalNode: {}, TimeCost: {}"
					, this.getClass().getName(), localNode, timeCost >= 1000 ? (timeCost / 1000.0) + "s" : timeCost + "ms");
		} else {
			log.info("Not leader, job didn't execute! JobName: {}, Leader: {}", this.getClass().getName(), leaderNode);
		}
	}

	protected void before() {

	}
	protected void after() {

	}
	protected abstract void executeTask();
	protected String getLockName() {
		return this.getClass().getName();
	}
	protected boolean getRelease() {
		return true;
	}

	protected LeaderElector getLeaderElector() {
		Class<? extends AbstractTask> clz = this.getClass();
		LeaderElector elector = ELECTOR_MAP_REF.get(clz);
		if (elector == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				if (field.getType() == LeaderElector.class) {
					field.setAccessible(true);
					try {
						elector = (LeaderElector) field.get(this);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					break;
				}
			}
			if (elector != null) {
				ELECTOR_MAP_REF.put(clz, elector);
			}
		}
		return elector;
	}
}
