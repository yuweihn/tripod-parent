package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.dao.mybatis.Dao;
import com.yuweix.tripod.permission.model.SysAdmin;

import java.util.List;


/**
 * @author yuwei
 */
public interface SysAdminDao extends Dao<SysAdmin, Long> {
	/**
	 * 数据来源于下面几个表，当这几个表的数据有变化，则清掉缓存中的当前数据
	 * SysAdmin
	 */
	SysAdmin findAdminByAccountNo(String accountNo);
	void deleteAdminFromCache(String accountNo);
	
	
	/**
	 * 查询某人是否有某权限
	 * 数据来源于下面几个表，当这几个表的数据有变化，则清掉缓存中的当前数据
	 * SysAdminRoleRel, SysRolePermissionRel
	 */
	boolean hasPermission(long adminId, long permissionId);
	void deleteHasPermissionFromCache(long adminId, long permissionId);

	List<Long> queryPermissionIdListByAdminId(long adminId);
	
	int findCountByRoleId(long roleId, String keywords);
	List<SysAdmin> findListByRoleId(long roleId, String keywords, Integer pageNo, Integer pageSize);
}
