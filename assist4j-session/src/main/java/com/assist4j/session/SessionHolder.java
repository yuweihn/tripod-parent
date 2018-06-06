package com.assist4j.session;


import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public class SessionHolder {
	private static volatile SessionHolder instance = null;
	private static final Lock lock = new ReentrantLock();


	private Set<String> sessionIdSet = new CopyOnWriteArraySet<String>();


	private SessionHolder() {

	}

	public static SessionHolder get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new SessionHolder();
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	public void add(String sessionId) {
		sessionIdSet.add(sessionId);
	}

	public void remove(String sessionId) {
		sessionIdSet.remove(sessionId);
	}
}
