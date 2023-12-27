# cachesession
Manage session by cache such as redis...

For example:
------------------------------------------------------------------------------------------------------------------
	@Bean
	public FilterRegistrationBean<Filter> cacheSessionFilterRegistrationBean(@Qualifier("redisCache") Cache cache
			, @Value("${cache.session.maxInactiveInterval}") int maxInactiveInterval
			, @Value("${cache.session.applicationName}") String applicationName
			, @Value("${cache.session.headerKey:token}") String headerKey
			, @Value("${cache.session.split:false}") boolean split
			, @Value("${cache.session.maxLength:1024}") int maxLength) {
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		GwSessionFilter filter = new GwSessionFilter();
		filter.setCache(new SessionCache() {
			@Override
			public boolean put(String key, String value, long timeout) {
				if (!split) {
					return cache.put(key, value, timeout);
				} else {
					return cache.putSplit(key, value, timeout, maxLength);
				}
			}
			
			@Override
			public String get(String key) {
				if (!split) {
					return cache.get(key);
				} else {
					return cache.getSplit(key);
				}
			}
			
			@Override
			public void remove(String key) {
				if (!split) {
					cache.remove(key);
				} else {
					cache.removeSplit(key);
				}
			}
		});
		filter.setMaxInactiveInterval(maxInactiveInterval);
		filter.setApplicationName(applicationName);
		filter.setKey(headerKey);
		bean.setFilter(filter);
		bean.setUrlPatterns(Arrays.asList("/*"));
		return bean;
	}
------------------------------------------------------------------------------------------------------------------

