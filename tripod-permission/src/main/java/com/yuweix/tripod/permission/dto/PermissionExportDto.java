package com.yuweix.tripod.permission.dto;


import com.yuweix.tripod.core.encrypt.SecurityUtil;
import com.yuweix.tripod.core.json.JsonUtil;

import java.io.Serializable;
import java.util.List;


/**
 * @author yuwei
 */
public class PermissionExportDto implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String SALT = "Rewfe5..;;43po6baasfpcxr034;;',";

	private List<PermissionDto> list;
	private String sign;

	public PermissionExportDto() {

	}
	public PermissionExportDto(List<PermissionDto> list) {
		this.setList(list);
	}


	public boolean verify() {
		return this.toSgin().equals(this.sign);
	}

	private String toSgin() {
		return SecurityUtil.getMd5(JsonUtil.toJSONString(list) + SALT);
	}

	public List<PermissionDto> getList() {
		return list;
	}

	public void setList(List<PermissionDto> list) {
		this.list = list;
		this.sign = this.toSgin();
	}

	public String getSign() {
		return sign;
	}
}
