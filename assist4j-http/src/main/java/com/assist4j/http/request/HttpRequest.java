package com.assist4j.http.request;


import com.assist4j.http.response.HttpResponse;


/**
 * @author yuwei
 */
public interface HttpRequest {
	<B>HttpResponse<B> execute();
}
