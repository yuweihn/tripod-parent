package com.yuweix.assist4j.schedule.base;


import com.yuweix.assist4j.schedule.LeaderElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
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

	private static SoftReference<Map<Class<? extends AbstractTask>, LeaderElectorWrapper>> ELECTOR_MAP_REF;
	private static final Object electorLock = new Object();
	private static final class LeaderElectorWrapper {
		private LeaderElector elector = null;
		LeaderElectorWrapper(LeaderElector elector) {
			this.elector = elector;
		}
	}
	private static final LeaderElectorWrapper EMPTY_ELECTOR = new LeaderElectorWrapper(null);

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
		LeaderElector.R res = leaderElector.tryAcquire(lockName);
		if (res.isSuccess()) {
			before();
			try {
				executeTask();
			} catch (Exception e) {
				handle(e);
			} finally {
				if (release) {
					try {
						leaderElector.release(lockName);
					} catch (Exception ignored) {
					}
				}
			}
			after();
			long timeCost = System.currentTimeMillis() - startTime;
			log.info("Job executed here, JobName: {}, LocalNode: {}, TimeCost: {}"
					, this.getClass().getName(), res.getLocal(), timeCost >= 1000 ? (timeCost / 1000.0) + "s" : timeCost + "ms");
		} else {
			log.warn("Not leader, job didn't execute! JobName: {}, Leader: {}", this.getClass().getName(), res.getLeader());
		}
	}

	protected void before() {

	}
	protected void handle(Throwable t) {
		log.error("{}", t.getMessage());
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

	private static Map<Class<? extends AbstractTask>, LeaderElectorWrapper> getElectorWrapperMap() {
		Map<Class<? extends AbstractTask>, LeaderElectorWrapper> map = null;
		if (ELECTOR_MAP_REF == null || (map = ELECTOR_MAP_REF.get()) == null) {
			synchronized (electorLock) {
				if (ELECTOR_MAP_REF == null || (map = ELECTOR_MAP_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					ELECTOR_MAP_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	protected LeaderElector getLeaderElector() {
		Class<? extends AbstractTask> clz = this.getClass();
		Map<Class<? extends AbstractTask>, LeaderElectorWrapper> map = getElectorWrapperMap();
		LeaderElectorWrapper wrapper = map.get(clz);
		if (wrapper == null) {
			synchronized (this) {
				wrapper = map.get(clz);
				if (wrapper == null) {
					wrapper = reflectElector(clz);
					map.put(clz, wrapper);
				}
			}
		}
		return wrapper.elector;
	}
	private LeaderElectorWrapper reflectElector(Class<?> clazz) {
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field f: fields) {
				if (f.getType() == LeaderElector.class) {
					f.setAccessible(true);
					try {
						return new LeaderElectorWrapper((LeaderElector) f.get(this));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return EMPTY_ELECTOR;
	}
}
