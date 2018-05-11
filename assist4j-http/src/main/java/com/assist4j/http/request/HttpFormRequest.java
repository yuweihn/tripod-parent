package com.assist4j.http.request;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.assist4j.http.DefaultHttpDelete;
import com.assist4j.http.HttpConstant;
import com.assist4j.http.HttpMethod;
import com.assist4j.http.response.ErrorHttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import com.assist4j.http.response.HttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * 表单请求
 * @author yuwei
 */
public class HttpFormRequest extends AbstractHttpRequest<HttpFormRequest> {
	private HttpMethod method;
	private List<FormField> fieldList;


	private HttpFormRequest() {
		super();
		fieldList = new ArrayList<FormField>();
		initMethod(HttpMethod.GET);
	}
	public static HttpFormRequest create() {
		return new HttpFormRequest();
	}


	public HttpFormRequest initMethod(HttpMethod method) {
		this.method = method;
		return this;
	}

	public HttpFormRequest initFieldList(Map<String, ? extends Object> map) {
		if(map == null || map.isEmpty()) {
			return this;
		}

		List<FormField> fieldList = new ArrayList<FormField>();
		for(Map.Entry<String, ? extends Object> entry: map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(key == null || value == null) {
				continue;
			}
			fieldList.add(new FormField(key, value));
		}
		return initFieldList(fieldList);
	}
	public HttpFormRequest initFieldList(List<FormField> fieldList) {
		this.fieldList.clear();
		this.fieldList.addAll(fieldList);
		return this;
	}
	public HttpFormRequest addField(String key, String value) {
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
			return this;
		}

		fieldList.add(new FormField(key, value));
		return this;
	}


	private <B>HttpResponse<B> doGet() {
		try {
			URIBuilder uriBuilder = new URIBuilder(this.getUrl());
			if(!CollectionUtils.isEmpty(fieldList)) {
				for (FormField ff: fieldList) {
					String k = ff.getKey();
					String v = ff.getValue();
					if(StringUtils.isEmpty(k) || StringUtils.isEmpty(v)) {
						continue;
					}
					uriBuilder.setParameter(k, v);
				}
			}

			URI uri = uriBuilder.build();
			this.setHttpUriRequest(new HttpGet(uri));
			return execute0();
		} catch (URISyntaxException e) {
			return new ErrorHttpResponse<B>(HttpStatus.SC_NOT_FOUND, e.getMessage());
		}
	}

	private static List<NameValuePair> toNameValuePairList(List<FormField> fieldList) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if(CollectionUtils.isEmpty(fieldList)) {
			return list;
		}

		for (FormField ff: fieldList) {
			String k = ff.getKey();
			String v = ff.getValue();
			if(StringUtils.isEmpty(k) || StringUtils.isEmpty(v)) {
				continue;
			}
			list.add(new BasicNameValuePair(k, v));
		}
		return list;
	}

	private <B>HttpResponse<B> doPost() {
		List<NameValuePair> list = toNameValuePairList(fieldList);
		
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;

		HttpPost post = new HttpPost(this.getUrl());
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, Charset.forName(charset));
		post.setEntity(entity);
		this.setHttpUriRequest(post);
		return execute0();
	}

	private <B>HttpResponse<B> doPut() {
		List<NameValuePair> list = toNameValuePairList(fieldList);
		
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;

		HttpPut put = new HttpPut(this.getUrl());
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, Charset.forName(charset));
		put.setEntity(entity);
		this.setHttpUriRequest(put);
		return execute0();
	}

	private <B>HttpResponse<B> doDelete() {
		List<NameValuePair> list = toNameValuePairList(fieldList);
		
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;

		DefaultHttpDelete del = new DefaultHttpDelete(this.getUrl());
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, Charset.forName(charset));
		del.setEntity(entity);
		this.setHttpUriRequest(del);
		return execute0();
	}


	@Override
	public <B>HttpResponse<B> execute() {
		if(method == null || HttpMethod.GET.equals(method)) {
			return doGet();
		}
		if(HttpMethod.POST.equals(method)) {
			return doPost();
		}
		if(HttpMethod.PUT.equals(method)) {
			return doPut();
		}
		if(HttpMethod.DELETE.equals(method)) {
			return doDelete();
		}
		return doPost();
	}

	@Override
	protected ContentType getHeaderContentType() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;
		return ContentType.APPLICATION_FORM_URLENCODED.withCharset(Charset.forName(charset));
	}
}
