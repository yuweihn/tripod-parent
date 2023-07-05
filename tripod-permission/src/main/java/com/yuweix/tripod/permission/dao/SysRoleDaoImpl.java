package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.permission.mapper.SysRoleMapper;
import com.yuweix.tripod.permission.model.SysRole;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.CacheableDao;
import com.yuweix.tripod.permission.common.Properties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;


/**
 * @author yuwei
 */
@Repository("sysRoleDao")
public class SysRoleDaoImpl extends CacheableDao<SysRole, Long> implements SysRoleDao {
	@Resource
	private SysRoleMapper sysRoleMapper;
	@Resource
	private Properties properties;


	private static final String CACHE_KEY_ROLE_BY_NO = "cache.%s.role.by.no.%s";


	@Override
	protected BaseMapper<SysRole, Long> getMapper() {
		return sysRoleMapper;
	}

	@Override
	protected String getAppName() {
		return properties.getAppName();
	}

	@Override
	protected void onchange(SysRole t) {
		deleteRoleByNoFromCache(t.getRoleNo());
	}

	@Override
	public SysRole queryRoleByNo(String roleNo) {
		String key = String.format(CACHE_KEY_ROLE_BY_NO, getAppName(), roleNo);
		SysRole role = cache.get(key);
		if (role != null) {
			return role;
		}

		role = sysRoleMapper.queryRoleByNo(roleNo);
		if (role != null) {
			cache.put(key, role, 1 * 60 * 60);
		}
		return role;
	}

	@Override
	public void deleteRoleByNoFromCache(String roleNo) {
		String key = String.format(CACHE_KEY_ROLE_BY_NO, getAppName(), roleNo);
		cache.remove(key);
	}
}
