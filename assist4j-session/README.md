# cachesession
Manage session by cache such as redis...

For example:
------------------------------------------------------------------------------------------------------------------
	@Bean(name = "sessionCache")
	public SessionCache sessionCache(@Qualifier("redisCache") Cache cache
			, @Value("${cache.session.key}") String cacheSessionKey
			, @Value("${cache.session.maxInactiveInterval}") int maxInactiveInterval
			, @Value("${cache.session.cookieSessionName}") String cookieSessionName) {
		return new SessionCache() {
			@Override
			public boolean put(String key, String value, long expiredTime) {
				return cache.put(key, value, expiredTime);
			}

			@Override
			public String get(String key) {
				return cache.get(key);
			}

			@Override
			public void remove(String key) {
				cache.remove(key);
			}

			@Override
			public int getMaxInactiveInterval() {
				return maxInactiveInterval;
			}

			@Override
			public String getCacheSessionKey() {
				return cacheSessionKey;
			}

			@Override
			public String getCookieSessionName() {
				return cookieSessionName;
			}

			@Override
			public void afterCompletion(String sessionId) {
				
			}
		};
	}

	@Bean
	public FilterRegistrationBean<SessionFilter> cacheSessionFilterRegistrationBean(@Qualifier("sessionCache") SessionCache sessionCache
			, @Value("${cache.session.key}") String cacheSessionKey
			, @Value("${cache.session.maxInactiveInterval}") int maxInactiveInterval
			, @Value("${cache.session.cookieSessionName}") String cookieSessionName) {
		FilterRegistrationBean<SessionFilter> bean = new FilterRegistrationBean<SessionFilter>();
		SessionFilter filter = new SessionFilter(sessionCache);
		bean.setFilter(filter);
		bean.setUrlPatterns(Arrays.asList("/*"));
		return bean;
	}
------------------------------------------------------------------------------------------------------------------

