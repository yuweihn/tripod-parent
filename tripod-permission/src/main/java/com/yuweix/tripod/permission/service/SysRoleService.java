package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.permission.dto.RoleDto;
import java.util.List;


/**
 * @author yuwei
 */
public interface SysRoleService {
	/**
	 * 查询所有角色
	 * @return
	 */
	List<RoleDto> queryAllRoleList();

	/**
	 * 添加角色
	 * 返回新加的角色id
	 */
	long addRole(String roleNo, String roleName, String creator);

	/**
	 * 查询角色个数
	 */
	int queryRoleCount(String keywords);
	/**
	 * 查询角色列表
	 */
	List<RoleDto> queryRoleList(String keywords, int pageNo, int pageSize);

	Long queryIdByRoleNo(String roleNo);
	RoleDto queryRoleById(long roleId);
	void updateRole(long id, String roleNo, String roleName, String modifier);
	void deleteRole(long roleId);
}
