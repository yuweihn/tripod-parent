package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.dao.mybatis.Dao;
import com.yuweix.tripod.permission.model.SysPermission;

import java.util.List;


/**
 * @author yuwei
 */
public interface SysPermissionDao extends Dao<SysPermission, Long> {
	int queryPermissionCount(List<Long> idList, Long parentId, String keywords, List<String> permTypeList
			, Boolean visible);
	List<SysPermission> queryPermissionList(List<Long> idList, Long parentId, String keywords, List<String> permTypeList
			, Boolean visible, Integer pageNo, Integer pageSize);

	SysPermission queryPermissionByNo(String permNo);
	void deletePermissionByNoFromCache(String permNo);

	int queryChildPermissionCount(long parentId);
}
