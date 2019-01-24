# cachesession
Manage session by cache such as redis...

For example:
------------------------------------------------------------------------------------------------------------------
	@Bean(name = "sessionCache")
	public SessionCache sessionCache(@Qualifier("redisCache") Cache cache) {
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
			public void afterCompletion(String sessionId) {
				
			}
		};
	}

	@Bean
	public FilterRegistrationBean cacheSessionFilterRegistrationBean(@Qualifier("sessionCache") SessionCache sessionCache
			, @Value("${cache.session.headerKey}") String headerKey
			, @Value("${cache.session.maxInactiveInterval}") int maxInactiveInterval
			, @Value("${cache.session.applicationName}") String applicationName
			, @Value("${cache.session.split:false}") boolean split
			, @Value("${cache.session.maxValueLength:1024}") int maxValueLength) {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		HeaderSessionFilter filter = new HeaderSessionFilter(sessionCache);
		filter.setKey(headerKey);
		filter.setMaxInactiveInterval(maxInactiveInterval);
		filter.setApplicationName(applicationName);
		filter.setValueSplit(split, maxValueLength);
		bean.setFilter(filter);
		bean.setUrlPatterns(Arrays.asList("/*"));
		bean.addInitParameter("exclusive", "/, /user/*");
		return bean;
	}
------------------------------------------------------------------------------------------------------------------

