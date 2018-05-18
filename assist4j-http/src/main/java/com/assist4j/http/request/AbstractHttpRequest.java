package com.assist4j.http.request;


import java.util.List;
import javax.servlet.http.Cookie;

import com.assist4j.http.CallbackResponseHandler;
import com.assist4j.http.DefaultHttpDelete;
import com.assist4j.http.HttpContextAdaptor;
import com.assist4j.http.HttpMethod;
import com.assist4j.http.response.ErrorHttpResponse;
import com.assist4j.http.strategy.connect.KeepAliveStrategy;
import com.assist4j.http.strategy.redirect.NeedRedirectStrategy;
import com.assist4j.http.strategy.retry.NotNeedRetryHandler;
import org.apache.http.Header;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import com.assist4j.http.ssl.TrustAllSslSocketFactory;
import com.assist4j.http.response.HttpResponse;
import org.springframework.util.CollectionUtils;


/**
 * @author yuwei
 */
public abstract class AbstractHttpRequest<T extends AbstractHttpRequest<T>> implements HttpRequest {
	private HttpUriRequest httpUriRequest;
	private String url;
	private HttpMethod method;
	private Class<?> responseBodyClass;
	private List<Cookie> cookieList;
	private List<Header> headerList;
	private LayeredConnectionSocketFactory sslSocketFactory;
	private ConnectionKeepAliveStrategy keepAliveStrategy;
	private RequestConfig requestConfig;
	private HttpRequestRetryHandler retryHandler;
	private RedirectStrategy redirectStrategy;
	private List<HttpRequestInterceptor> firstRequestInterceptorList;
	private List<HttpRequestInterceptor> lastRequestInterceptorList;
	private List<HttpResponseInterceptor> firstResponseInterceptorList;
	private List<HttpResponseInterceptor> lastResponseInterceptorList;
	private String charset;


	protected AbstractHttpRequest() {
		this.responseBodyClass = String.class;
	}

	protected void setHttpUriRequest(HttpUriRequest httpUriRequest) {
		this.httpUriRequest = httpUriRequest;
	}


	@SuppressWarnings("unchecked")
	public T initUrl(String url) {
		this.url = url;
		return (T) this;
	}
	public String getUrl() {
		return url;
	}

	@SuppressWarnings("unchecked")
	public T initMethod(HttpMethod method) {
		this.method = method;
		return (T) this;
	}
	public HttpMethod getMethod() {
		return method;
	}

	@SuppressWarnings("unchecked")
	public T initResponseBodyClass(Class<?> responseBodyClass) {
		this.responseBodyClass = responseBodyClass;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initCookieList(List<Cookie> cookieList) {
		this.cookieList = cookieList;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initHeaderList(List<Header> headerList) {
		this.headerList = headerList;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initSslSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initConnectionKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
		this.keepAliveStrategy = keepAliveStrategy;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initRetryHandler(HttpRequestRetryHandler retryHandler) {
		this.retryHandler = retryHandler;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initFirstRequestInterceptorList(List<HttpRequestInterceptor> firstRequestInterceptorList) {
		this.firstRequestInterceptorList = firstRequestInterceptorList;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initLastRequestInterceptorList(List<HttpRequestInterceptor> lastRequestInterceptorList) {
		this.lastRequestInterceptorList = lastRequestInterceptorList;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initFirstResponseInterceptorList(List<HttpResponseInterceptor> firstResponseInterceptorList) {
		this.firstResponseInterceptorList = firstResponseInterceptorList;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initLastResponseInterceptorList(List<HttpResponseInterceptor> lastResponseInterceptorList) {
		this.lastResponseInterceptorList = lastResponseInterceptorList;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T initCharset(String charset) {
		this.charset = charset;
		return (T) this;
	}
	public String getCharset() {
		return charset;
	}

	protected ContentType getHeaderContentType() {
		return null;
	}

	protected HttpEntityEnclosingRequestBase getRequestBase() {
		if (HttpMethod.POST.equals(method)) {
			return new HttpPost(url);
		} else if (HttpMethod.PUT.equals(method)) {
			return new HttpPut(url);
		} else if (HttpMethod.DELETE.equals(method)) {
			return new DefaultHttpDelete(url);
		} else {
			return new HttpPost(url);
		}
	}

	@SuppressWarnings("unchecked")
	protected <B>HttpResponse<B> execute0() {
		/**
		 * header
		 */
		ContentType hct = getHeaderContentType();
		if (hct != null) {
			httpUriRequest.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, hct.toString()));
		}
		if (!CollectionUtils.isEmpty(headerList)) {
			for(Header header: headerList) {
				httpUriRequest.setHeader(header);
			}
		}
		/**
		 * cookie
		 */
		if (!CollectionUtils.isEmpty(cookieList)) {
			StringBuilder builder = new StringBuilder("");
			for (Cookie cookie: cookieList) {
				builder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
			}
			builder.deleteCharAt(builder.length() - 1);
			httpUriRequest.setHeader("Cookie", builder.toString());
		}
		/**
		 * context
		 */
		HttpContextAdaptor context = HttpContextAdaptor.create();

		HttpClientBuilder builder = HttpClients.custom()
											.setDefaultRequestConfig(requestConfig)
											.setKeepAliveStrategy(keepAliveStrategy == null ? KeepAliveStrategy.get() : keepAliveStrategy)
											.setDefaultCookieStore(context.getCookieStore())
											.setRetryHandler(retryHandler == null ? NotNeedRetryHandler.get() : retryHandler)
											.setRedirectStrategy(redirectStrategy == null ? NeedRedirectStrategy.get() : redirectStrategy)
											.setSSLSocketFactory(sslSocketFactory == null ? TrustAllSslSocketFactory.get() : sslSocketFactory);

		/**
		 * add first http request interceptor list
		 */
		if (!CollectionUtils.isEmpty(firstRequestInterceptorList)) {
			for (HttpRequestInterceptor interceptor: firstRequestInterceptorList) {
				builder.addInterceptorFirst(interceptor);
			}
		}

		/**
		 * add last http request interceptor list
		 */
		if (!CollectionUtils.isEmpty(lastRequestInterceptorList)) {
			for (HttpRequestInterceptor interceptor: lastRequestInterceptorList) {
				builder.addInterceptorLast(interceptor);
			}
		}

		/**
		 * add first http response interceptor list
		 */
		if (!CollectionUtils.isEmpty(firstResponseInterceptorList)) {
			for (HttpResponseInterceptor interceptor: firstResponseInterceptorList) {
				builder.addInterceptorFirst(interceptor);
			}
		}

		/**
		 * add last http response interceptor list
		 */
		if (!CollectionUtils.isEmpty(lastResponseInterceptorList)) {
			for (HttpResponseInterceptor interceptor: lastResponseInterceptorList) {
				builder.addInterceptorLast(interceptor);
			}
		}

		CloseableHttpClient client = builder.build();
		CallbackResponseHandler handler = CallbackResponseHandler.create()
																.initBodyClass(responseBodyClass)
																.initContext(context)
																.initCharset(charset);
		try {
			return (HttpResponse<B>) client.execute(httpUriRequest, handler, context);
		} catch (Exception e) {
			return new ErrorHttpResponse<B>(HttpStatus.SC_SERVICE_UNAVAILABLE, e.toString());
		}
	}
}
