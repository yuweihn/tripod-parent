# cachesession
Manage session by cache such as redis...

For example:
-----------------------------------------------------------------------------------------------------------------------------------------------
	@Bean(name = "sessionCache")
	public SessionCache sessionCache() {
		return new SessionCache() {
			@Override
			public boolean put(String key, String value, long expiredTime) {
				//TODO
				return null;
			}
			
			@Override
			public String get(String key) {
				//TODO
				return null;
			}
			
			@Override
			public void remove(String key) {
				//TODO
			}
		};
	}
	
	@Bean
	public FilterRegistrationBean cacheSessionFilterRegistrationBean(@Qualifier("sessionCache") SessionCache sessionCache
			, @Value("${cache.session.key}") String cacheSessionKey
			, @Value("${cache.session.maxInactiveInterval}") int maxInactiveInterval
			, @Value("${cache.session.cookieSessionName}") String cookieSessionName) {
		FilterRegistrationBean registBean = new FilterRegistrationBean();
		CacheSessionFilter filter = new CacheSessionFilter(sessionCache, cacheSessionKey, maxInactiveInterval, cookieSessionName);
		registBean.setFilter(filter);
		registBean.setUrlPatterns(Arrays.asList("/*"));
		return registBean;
	}
-----------------------------------------------------------------------------------------------------------------------------------------------

