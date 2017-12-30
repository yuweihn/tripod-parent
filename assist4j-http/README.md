# assist4j-http

For example:
-----------------------------------------------------------------------------------------------------------------------------------------------
	private static void downloadJira() {
		String url = "http://jira.zhonganonline.com/secure/attachment/43511/%E6%8B%92%E8%B5%94%E9%80%9A%E7%9F%A5%E4%B9%A6%282%29.wps";
		List<Header> headerList = new ArrayList<Header>();
		headerList.add(new BasicHeader("jira-user", "api_archives"));
		headerList.add(new BasicHeader("token", "aXgNiEaaok2639q7MR"));
		HttpResponse<byte[]> response = HttpFormRequest.create().initUrl(url)
															.initHeaderList(headerList)
															.initResponseBodyClass(byte[].class)
															.initRequestConfig(RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(1000).setSocketTimeout(1000).build())
															.execute();
		if(!response.isSuccess()) {
			System.out.println(response.getErrorMessage());
		} else {
			byte[] body = response.getBody();
			System.out.println(body.length);
		}
	}
-----------------------------------------------------------------------------------------------------------------------------------------------
