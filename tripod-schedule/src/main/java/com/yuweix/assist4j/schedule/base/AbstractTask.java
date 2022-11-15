package com.yuweix.assist4j.schedule.base;


import com.yuweix.assist4j.data.elect.Elector;
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
	private static final Elector DEFAULT_LEADER_ELECTOR = new Elector() {
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

	private static SoftReference<Map<Class<? extends AbstractTask>, ElectorWrapper>> ELECTOR_MAP_REF;
	private static final Object electorLock = new Object();
	private static final class ElectorWrapper {
		private Elector elector = null;
		ElectorWrapper(Elector elector) {
			this.elector = elector;
		}
	}
	private static final ElectorWrapper EMPTY_ELECTOR = new ElectorWrapper(null);

	public AbstractTask() {

	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		Elector elector = getLeaderElector();
		if (elector == null) {
			elector = DEFAULT_LEADER_ELECTOR;
		}

		String lockName = getLockName();
		boolean release = getRelease();
		Elector.R res = elector.tryAcquire(lockName);
		if (res.isSuccess()) {
			before();
			try {
				executeTask();
			} catch (Exception e) {
				handle(e);
			} finally {
				if (release) {
					try {
						elector.release(lockName);
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

	private static Map<Class<? extends AbstractTask>, ElectorWrapper> getElectorWrapperMap() {
		Map<Class<? extends AbstractTask>, ElectorWrapper> map = null;
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
	protected Elector getLeaderElector() {
		Class<? extends AbstractTask> clz = this.getClass();
		Map<Class<? extends AbstractTask>, ElectorWrapper> map = getElectorWrapperMap();
		ElectorWrapper wrapper = map.get(clz);
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
	private ElectorWrapper reflectElector(Class<?> clazz) {
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field f: fields) {
				if (f.getType() == Elector.class) {
					f.setAccessible(true);
					try {
						return new ElectorWrapper((Elector) f.get(this));
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
