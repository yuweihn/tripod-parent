package com.yuweix.tripod.permission.common;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;


/**
 * @author yuwei
 */
@Component
@ConfigurationProperties(prefix = "tripod.permission.setting", ignoreUnknownFields = true)
public class Properties implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String appName;

	private String actionClass;
	private String actionMethod;

	private String successCode = "0000";
	private String failureCode = "9999";
	private String noAuthorityCode = "1005";


	public String getAppName() {
		if (appName == null) {
			throw new RuntimeException("[appName] is required.");
		}
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getActionClass() {
		if (actionClass == null) {
			throw new RuntimeException("[actionClass] is required.");
		}
		return actionClass;
	}

	public void setActionClass(String actionClass) {
		this.actionClass = actionClass;
	}

	public String getActionMethod() {
		if (actionMethod == null) {
			throw new RuntimeException("[actionMethod] is required.");
		}
		return actionMethod;
	}

	public void setActionMethod(String actionMethod) {
		this.actionMethod = actionMethod;
	}

	public String getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(String successCode) {
		this.successCode = successCode;
	}

	public String getFailureCode() {
		return failureCode;
	}

	public void setFailureCode(String failureCode) {
		this.failureCode = failureCode;
	}

	public String getNoAuthorityCode() {
		return noAuthorityCode;
	}

	public void setNoAuthorityCode(String noAuthorityCode) {
		this.noAuthorityCode = noAuthorityCode;
	}
}
