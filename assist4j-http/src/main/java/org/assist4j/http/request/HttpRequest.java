package org.assist4j.http.request;


import org.assist4j.http.response.HttpResponse;


/**
 * @author yuwei
 */
public interface HttpRequest {
	<B>HttpResponse<B> execute();
}
