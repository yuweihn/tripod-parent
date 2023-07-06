package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.dao.mybatis.Dao;
import com.yuweix.tripod.permission.model.SysRolePermissionRel;

import java.util.List;


/**
 * @author yuwei
 */
public interface SysRolePermissionRelDao extends Dao<SysRolePermissionRel, Long> {
	List<SysRolePermissionRel> queryListByRoleId(long roleId);

	List<Long> queryPermIdListByRoleId(long roleId);

	/**
	 * 数据来源于下面几个表，当这几个表的数据有变化，则清掉缓存中的当前数据
	 * SysRolePermissionRel
	 */
	SysRolePermissionRel queryByRoleIdAndPermId(long roleId, long permId);
	void deleteByRoleIdAndPermIdFromCache(long roleId, long permId);
}
