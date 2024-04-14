package com.yuweix.tripod.sharding.aspect;



/**
 * @author yuwei
 */
public class DynamicTableTL {
	private static final ThreadLocal<TB> THREAD_LOCAL = new ThreadLocal<>();

	public static void set(String srcName, String targetName) {
		THREAD_LOCAL.set(new TB(srcName, targetName));
	}

	public static void remove() {
		THREAD_LOCAL.remove();
	}

	public static String getSrcName() {
		final TB tb = THREAD_LOCAL.get();
		return tb == null ? null : tb.srcName;
	}

	public static String getTargetName() {
		final TB tb = THREAD_LOCAL.get();
		return tb == null ? null : tb.targetName;
	}

	private static class TB {
		private String srcName;
		private String targetName;

		public TB(String srcName, String targetName) {
			this.srcName = srcName;
			this.targetName = targetName;
		}
	}
}
