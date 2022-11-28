package com.yuweix.tripod.dao.hibernate;



/**
 * 动态修改表名
 * @author yuwei
 */
public class DynamicTableThreadLocal {
	private static final ThreadLocal<TB> THREAD_LOCAL = new ThreadLocal<>();

	public static void set(String srcName, String destName) {
		THREAD_LOCAL.set(new TB(srcName, destName));
	}

	public static void remove() {
		THREAD_LOCAL.remove();
	}

	public static String getSrcName() {
		final TB tb = THREAD_LOCAL.get();
		return tb == null ? null : tb.srcName;
	}

	public static String getDestName() {
		final TB tb = THREAD_LOCAL.get();
		return tb == null ? null : tb.destName;
	}

	private static class TB {
		private String srcName;
		private String destName;

		public TB(String srcName, String destName) {
			this.srcName = srcName;
			this.destName = destName;
		}
	}
}
