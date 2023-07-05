package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.CacheableDao;
import com.yuweix.tripod.permission.conf.Properties;
import com.yuweix.tripod.permission.mapper.SysAdminMapper;
import com.yuweix.tripod.permission.model.SysAdmin;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author yuwei
 */
@Repository("sysAdminDao")
public class SysAdminDaoImpl extends CacheableDao<SysAdmin, Long> implements SysAdminDao {
	@Resource
	private SysAdminMapper sysAdminMapper;
	@Resource
	private Properties properties;
	
	
	private static final String CACHE_KEY_ADMIN_BY_ACCOUNT_NO = "cache.%s.admin.by.account.no.%s";
	private static final String CACHE_KEY_HAS_PERMISSION_BY_ADMIN_ID_AND_PERMISSION_ID = "cache.%s.has.permission.by.admin.%s.and.permission.%s";
	
	
	@Override
	protected BaseMapper<SysAdmin, Long> getMapper() {
		return sysAdminMapper;
	}

	@Override
	protected String getAppName() {
		return properties.getAppName();
	}

	@Override
	protected void onchange(SysAdmin t) {
		deleteAdminFromCache(t.getAccountNo());
	}
	
	
	@Override
	public SysAdmin findAdminByAccountNo(String accountNo) {
		String key = String.format(CACHE_KEY_ADMIN_BY_ACCOUNT_NO, getAppName(), accountNo);
		SysAdmin admin = cache.get(key);
		if (admin != null) {
			return admin;
		}
		
		admin = sysAdminMapper.findAdminByAccountNo(accountNo);
		if (admin != null) {
			cache.put(key, admin, 1 * 60 * 60);
		}
		return admin;
	}
	
	@Override
	public void deleteAdminFromCache(String accountNo) {
		String key = String.format(CACHE_KEY_ADMIN_BY_ACCOUNT_NO, getAppName(), accountNo);
		cache.remove(key);
	}

	@Override
	public boolean hasPermission(long adminId, long permissionId) {
		String key = String.format(CACHE_KEY_HAS_PERMISSION_BY_ADMIN_ID_AND_PERMISSION_ID, getAppName(), adminId, permissionId);
		Boolean has = cache.get(key);
		if (has != null) {
			return has;
		}
		
		has = sysAdminMapper.hasPermission(adminId, permissionId);
		cache.put(key, has, 5 * 60 * 60);
		return has;
	}

	@Override
	public void deleteHasPermissionFromCache(long adminId, long permissionId) {
		String key = String.format(CACHE_KEY_HAS_PERMISSION_BY_ADMIN_ID_AND_PERMISSION_ID, getAppName(), adminId, permissionId);
		cache.remove(key);
	}

	@Override
	public List<Long> queryPermissionIdListByAdminId(long adminId) {
		return sysAdminMapper.queryPermissionIdListByAdminId(adminId);
	}

	@Override
	public int findCountByRoleId(long roleId, String keywords) {
		return sysAdminMapper.findCountByRoleId(roleId, keywords);
	}

	@Override
	public List<SysAdmin> findListByRoleId(long roleId, String keywords, Integer pageNo, Integer pageSize) {
		return sysAdminMapper.findListByRoleId(roleId, keywords, pageNo, pageSize);
	}
}
