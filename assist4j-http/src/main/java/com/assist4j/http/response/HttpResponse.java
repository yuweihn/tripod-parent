package com.assist4j.http.response;


import org.apache.http.Header;
import org.apache.http.HttpStatus;

import javax.servlet.http.Cookie;
import java.util.List;


/**
 * @author yuwei
 */
public interface HttpResponse<B> {
	default boolean isSuccess() {
		return HttpStatus.SC_OK == getStatus();
	}
	
	int getStatus();
	String getErrorMessage();
	B getBody();
	List<Cookie> getCookieList();
	List<Header> getHeaderList();
	String getContentType();
}
