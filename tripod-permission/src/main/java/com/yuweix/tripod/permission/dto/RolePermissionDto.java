package com.yuweix.tripod.permission.dto;


import java.io.Serializable;
import java.util.List;


/**
 * @author yuwei
 */
public class RolePermissionDto implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private List<PermissionDto> permList;
	private List<Long> checkedPermIdList;

	public List<PermissionDto> getPermList() {
		return permList;
	}

	public void setPermList(List<PermissionDto> permList) {
		this.permList = permList;
	}

	public List<Long> getCheckedPermIdList() {
		return checkedPermIdList;
	}

	public void setCheckedPermIdList(List<Long> checkedPermIdList) {
		this.checkedPermIdList = checkedPermIdList;
	}
}

