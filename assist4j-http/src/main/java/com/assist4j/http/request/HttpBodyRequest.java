package com.assist4j.http.request;


import com.assist4j.http.HttpConstant;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import com.assist4j.http.response.HttpResponse;


/**
 * body请求
 * @author yuwei
 */
public class HttpBodyRequest extends AbstractHttpRequest<HttpBodyRequest> {
	private String content;
	private ContentType contentType;


	private HttpBodyRequest() {
		super();
		this.contentType = ContentType.APPLICATION_JSON;
	}
	public static HttpBodyRequest create() {
		return new HttpBodyRequest();
	}


	public HttpBodyRequest initContent(String content) {
		this.content = content;
		return this;
	}
	public HttpBodyRequest initContentType(ContentType contentType) {
		this.contentType = contentType;
		return this;
	}


	@Override
	public <B>HttpResponse<B> execute() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;
		
		HttpPost post = new HttpPost(this.getUrl());
		StringEntity entity = new StringEntity(content, charset);
		entity.setContentType(contentType.getMimeType());
		entity.setContentEncoding(charset);
		post.setEntity(entity);
		this.setHttpUriRequest(post);
		return execute0();
	}

	@Override
	protected ContentType getHeaderContentType() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;
		return contentType == null ? null : contentType.withCharset(charset);
	}
}
