package org.assist4j.http;


import org.assist4j.http.request.HttpRequest;
import org.assist4j.http.response.HttpResponse;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
@Deprecated
public abstract class HttpUtil {
	/**
	 * @deprecated Use {@link HttpRequest#execute()} instead.
	 */
	public static <B>HttpResponse<B> execute(HttpRequest request) {
		Assert.notNull(request, "[request] is required.");
		return request.execute();
	}
}
