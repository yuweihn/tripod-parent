package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.permission.dto.AdminDto;

import java.util.List;


/**
 * @author yuwei
 */
public interface AdminService {
	long createAccount(String accountNo, String password, String realName, Byte gender, String creator);
	void updateAccount(long id, String realName, Byte gender, String modifier);
	AdminDto findAdminById(long id);
	Response<Boolean, AdminDto> login(String accountNo, String password);
	void changePassword(long id, String password, String modifier);
	void changePassword(long id, String oldPassword, String password, String modifier);
	void changeAvatar(long id, String avatar, String modifier);
	void deleteAccount(long id);
	int queryAdminCount(String keywords);
	List<AdminDto> queryAdminList(String keywords, int pageNo, int pageSize);
	
	/**
	 * 查询某人是否有某权限
	 * @param adminId
	 * @param permissionId
	 * @return
	 */
	boolean hasPermission(long adminId, long permissionId);

	List<Long> queryPermissionIdListByAdminId(long adminId);
}
