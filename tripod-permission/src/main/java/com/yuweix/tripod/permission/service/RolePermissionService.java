package com.yuweix.tripod.permission.service;


import java.util.List;


/**
 * @author yuwei
 */
public interface RolePermissionService {
	List<Long> queryPermissionIdListByRoleId(long roleId);

	void saveRolePermission(long roleId, List<Long> permIdList, String modifier);
}
