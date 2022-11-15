package com.yuweix.tripod.http.request;


import com.yuweix.tripod.http.response.HttpResponse;


/**
 * @author yuwei
 */
public interface HttpRequest {
	<B>HttpResponse<B> execute();
}
