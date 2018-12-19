package com.assist4j.session;


import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.assist4j.session.cache.SessionCache;


/**
 * 一个HttpSession的实现，实际的属性会储存在指定的缓存实现中。
 * @author yuwei
 */
@SuppressWarnings("deprecation")
public class CacheHttpSession implements HttpSession {
	private String id;
	/**
	 * 缓存引擎
	 */
	private ProxySessionCache proxyCache;
	/**
	 * session失效时间(分钟)
	 */
	private int maxInactiveInterval;
	/**
	 * session是否已失效
	 */
	private boolean invalid = false;
	/**
	 * 存入cache的session的key
	 */
	private final String fullSessionId;
	/**
	 * 如果要避免重复登录，后登录的覆盖之前登录的，如果sessionIdKey为空，表明不需要处理重复登录的问题。
	 * sessionIdKey用于记录存入cache的session的key。
	 * 如用户编号为1001，登录成功后在cache中存入两条数据：
	 * 1、cache.assist4j.session.008c203973554062b2b04cd3535661f1={......}，
	 * 2、cache.assist4j.session.current.1001=cache.assist4j.session.008c203973554062b2b04cd3535661f1
	 * 其中cache.assist4j.session.current.1001即为sessionIdKey
	 */
	private String sessionIdKey;
	private String sessionIdKeyPre;
	private SessionAttribute sessionAttribute;



	/**
	 * 初始化时必须指定一个id字符串和缓存引擎实现。
	 * @param id id字符串。
	 * @param cache 缓存引擎。
	 */
	public CacheHttpSession(String id, SessionCache cache) {
		this.id = id;
		this.maxInactiveInterval = cache.getMaxInactiveInterval();
		if (this.maxInactiveInterval <= 0) {
			this.maxInactiveInterval = SessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL;
		}
		this.proxyCache = new ProxySessionCache(cache);
		this.sessionIdKeyPre = cache.getCacheSessionKey().trim() + "." + SessionConstant.SESSION_ID_KEY_CURRENT;
		this.fullSessionId = cache.getCacheSessionKey().trim() + "." + this.id;
		init();
	}

	/**
	 * 此Session的ID值。
	 * 这里返回存入cache的key值
	 * @return id值。
	 */
	@Override
	public String getId() {
		return fullSessionId;
	}

	/**
	 * 获取此Session的创建时间。
	 * @return 创建时间。
	 */
	@Override
	public long getCreationTime() {
		return sessionAttribute.getCreateTime().getTime();
	}

	/**
	 * 获取最后访问时间。
	 * @return 最后访问时间。
	 */
	@Override
	public long getLastAccessedTime() {
		return sessionAttribute.getLastAccessTime().getTime();
	}

	/**
	 * 获取当前的属性键值对。
	 * @return 属性键值对。
	 */
	public SessionAttribute getSessionAttribute() {
		return sessionAttribute;
	}

	/**
	 * 表示此Session被访问。
	 */
	public void access() {
		sessionAttribute.setLastAccessTime(Calendar.getInstance().getTime());
	}

	/**
	 * 设定Session的最长不活动时限(分钟），如果此时限没有活动的Session将被删除。
	 * @param maxInactiveInterval 最长活动时限。
	 */
	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	/**
	 * 获取最长不活动时限(分钟）。
	 * @return 最长活动时限。
	 */
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * 获取属性值。
	 * @param attributeName 属性名称。
	 * @return 属性值。
	 */
	@Override
	public Object getAttribute(String attributeName) {
		checkSessionInvalid();
		return sessionAttribute.getAttribute(attributeName);
	}

	/**
	 * 更新属性。
	 * @param attributeName 属性名称。
	 * @param attributeValue 属性值。
	 */
	@Override
	public void setAttribute(String attributeName, Object attributeValue) {
		checkSessionInvalid();
		if (attributeValue instanceof RepeatKey) {
			RepeatKey rlk = (RepeatKey) attributeValue;
			sessionAttribute.putAttribute(attributeName, rlk.getValue());
			this.sessionIdKey = sessionIdKeyPre + "." + rlk.getValue();
			sessionAttribute.setRepeatKey(attributeName);
			sessionAttribute.setRepeatValue(rlk.getValue());
		} else {
			sessionAttribute.putAttribute(attributeName, attributeValue);
		}
	}

	/**
	 * 移除已有的属性。
	 * @param attributeName 属性名称。
	 */
	@Override
	public void removeAttribute(String attributeName) {
		checkSessionInvalid();
		sessionAttribute.removeAttribute(attributeName);
	}

	/**
	 * Session过期。
	 */
	@Override
	public void invalidate() {
		setInvalid(true);
	}

	private void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * 判断是否已经超过了最大活动时间。
	 * @return true超过，false没有超过。
	 */
	public boolean isInvalid() {
		if (invalid) {
			return invalid;
		} else {
			if (getMaxInactiveInterval() <= 0) {
				setInvalid(false);
			} else {
				long invalidMillis = getMaxInactiveInterval() * 60 * 1000;
				long lastAccessTime = getLastAccessedTime();
				long now = Calendar.getInstance().getTimeInMillis();
				setInvalid((now - lastAccessTime) > invalidMillis);
			}
			
			return invalid;
		}
	}

	/**
	 * 判断此Session是否为新的。
	 * @return true 为新的，false为非新的。
	 */
	@Override
	public boolean isNew() {
		checkSessionInvalid();
		return sessionAttribute.isNewBuild();
	}

	@Override
	public int hashCode() {
		return fullSessionId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CacheHttpSession)) {
			return false;
		}
		
		CacheHttpSession other = (CacheHttpSession) obj;
		if (id == null && other.id == null) {
			return true;
		} if (id != null && other.id != null) {
			return id.equals(other.id);
		}
		return false;
	}

	public void removeSessionFromCache(){
		proxyCache.remove0(fullSessionId);
		if (sessionIdKey != null) {
			proxyCache.remove0(sessionIdKey);
		}
	}

	/**
	 * 更新当前Session至缓存。如果已经失效，从缓存中删除。
	 * @return sessionId
	 */
	public String sync() {
		if (isInvalid() || sessionAttribute == null || sessionAttribute.isEmpty()) {
			removeSessionFromCache();
			return fullSessionId;
		}

		proxyCache.put0(fullSessionId, SessionAttribute.encode(sessionAttribute));
		/**
		 * 如果sessionIdKey不为空，表明需要避免重复登录
		 */
		if (sessionIdKey != null) {
			/**
			 * 把当前账号之前登录的session清除掉，防止重复登录
			 */
			String sessionId = proxyCache.get0(sessionIdKey);
			if (sessionId != null && !sessionId.equals(fullSessionId)) {
				proxyCache.remove0(sessionId);
			}
			proxyCache.put0(sessionIdKey, fullSessionId);
		}
		return fullSessionId;
	}

	/**
	 * 初始化方法。
	 * 初始化缓存中的属性容器。
	 * 如果缓存中没有，则新建并设定创建时间和最后访问时间为当前时间和为新的会话。
	 */
	private void init() {
		findSessionAttribute();
		Object repeatValue = sessionAttribute.getRepeatValue();
		if (repeatValue != null && !"".equals(repeatValue.toString())) {
			this.sessionIdKey = sessionIdKeyPre + "." + repeatValue;
		}
	}

	/**
	 * 查找一个缓存中的属性储存bean.如果不存在将返回一个新的空bean.
	 * @return 用户Session属性键键值对储存bean.
	 */
	private void findSessionAttribute() {
		if (sessionAttribute != null) {
			return;
		}
		
		sessionAttribute = SessionAttribute.decode(proxyCache.get0(fullSessionId));
		if (sessionAttribute == null) {
			removeSessionFromCache();
			sessionAttribute = new SessionAttribute();
			Calendar now = Calendar.getInstance();
			sessionAttribute.setCreateTime(now.getTime());
			sessionAttribute.setLastAccessTime(now.getTime());
			sessionAttribute.setNewBuild(true);
		} else {
			sessionAttribute.setNewBuild(false);
		}
	}


	/**
	 * 判断当前Session是否已经失效.
	 * @throws IllegalStateException Session已经失效的异常.
	 */
	private void checkSessionInvalid() throws IllegalStateException {
		if (invalid) {
			throw new IllegalStateException("Session is invalid.");
		}
	}


	@Override
	public Enumeration<String> getAttributeNames() {
		return new SessionEnumeration(sessionAttribute.getAttributeNames());
	}
	private class SessionEnumeration implements Enumeration<String> {
		private Iterator<String> iterator;
		public SessionEnumeration (Set<String> _attributeNames) {
			Set<String> attributeNames = new HashSet<String>();
			if (_attributeNames != null && _attributeNames.size() > 0) {
				attributeNames.addAll(_attributeNames);
			}
			iterator = attributeNames.iterator();
		}

		@Override
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		@Override
		public String nextElement() {
			return iterator.next();
		}
	}
	

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getValue(String name) {
		return null;
	}

	@Override
	public String[] getValueNames() {
		return null;
	}

	@Override
	public void putValue(String name, Object value) {
		
	}

	@Override
	public void removeValue(String name) {
		
	}

	private class ProxySessionCache {
		private SessionCache target;
		public ProxySessionCache(SessionCache target) {
			this.target = target;
		}


		public boolean put0(String key, String value) {
			return target.put(key, value, maxInactiveInterval * 60);
		}

		public String get0(String key) {
			Object obj = target.get(key);
			if (obj instanceof String) {
				return (String) obj;
			}
			target.remove(key);
			return null;
		}

		public void remove0(String key) {
			target.remove(key);
		}
	}
}
