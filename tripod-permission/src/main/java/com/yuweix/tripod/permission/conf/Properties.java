package com.yuweix.tripod.permission.conf;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;


/**
 * @author yuwei
 */
@Component
@ConfigurationProperties(prefix = "tripod.permission.conf", ignoreUnknownFields = true)
public class Properties implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String appName;


	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
