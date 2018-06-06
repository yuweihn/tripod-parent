package com.assist4j.session;





/**
 * @author yuwei
 */
public abstract class SessionConstant {
	public static final String SESSION_ID_KEY_CURRENT = "current";

	/**
	 * 默认session失效时间(分钟)
	 */
	public static final int DEFAULT_MAX_INACTIVE_INTERVAL = 30;

	/**
	 * 为了便于统计session，将所有sessionId存入一个list，然后将该list存入cache中，以SESSION_ID_LIST_KEY为键。
	 */
	public static final String SESSION_ID_LIST_KEY = "session.constant.cache.session.id.list";
}
