package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.dao.mybatis.Dao;
import com.yuweix.tripod.permission.dto.SysAdminRoleDto;
import com.yuweix.tripod.permission.model.SysAdminRoleRel;

import java.util.List;


/**
 * @author yuwei
 */
public interface SysAdminRoleRelDao extends Dao<SysAdminRoleRel, Long> {
	/**
	 * 数据来源于下面几个表，当这几个表的数据有变化，则清掉缓存中的当前数据
	 * SysAdminRoleRel
	 */
	boolean hasRole(long adminId, long roleId);
	void deleteHasRoleFromCache(long adminId, long roleId);
	
	int queryAdminRoleCountByAdminId(Long adminId, Long roleId, String keywords);
	List<SysAdminRoleDto> queryAdminRoleListByAdminId(Long adminId, Long roleId, String keywords, int pageNo, int pageSize);
	
	/**
	 * 数据来源于下面几个表，当这几个表的数据有变化，则清掉缓存中的当前数据
	 * SysAdminRoleRel
	 */
	SysAdminRoleRel queryByAdminIdAndRoleId(long adminId, long roleId);
	void deleteByAdminIdAndRoleIdFromCache(long adminId, long roleId);
}
