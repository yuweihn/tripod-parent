package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.permission.mapper.SysPermissionMapper;
import com.yuweix.tripod.permission.model.SysPermission;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.CacheableDao;
import com.yuweix.tripod.permission.common.Properties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author yuwei
 */
@Repository("sysPermissionDao")
public class SysPermissionDaoImpl extends CacheableDao<SysPermission, Long> implements SysPermissionDao {
	@Resource
	private SysPermissionMapper sysPermissionMapper;
	@Resource
	private Properties properties;


	private static final String CACHE_KEY_PERMISSION_BY_NO = "cache.%s.permission.by.no.%s";


	@Override
	protected BaseMapper<SysPermission, Long> getMapper() {
		return sysPermissionMapper;
	}

	@Override
	protected String getAppName() {
		return properties.getAppName();
	}

	@Override
	protected void onchange(SysPermission t) {
		deletePermissionByNoFromCache(t.getPermNo());
	}


	@Override
	public int queryPermissionCount(List<Long> idList, Long parentId, String keywords, List<String> permTypeList
			, Boolean visible) {
		return sysPermissionMapper.queryPermissionCount(idList, parentId, keywords, permTypeList, visible);
	}

	@Override
	public List<SysPermission> queryPermissionList(List<Long> idList, Long parentId, String keywords, List<String> permTypeList
			, Boolean visible, Integer pageNo, Integer pageSize) {
		return sysPermissionMapper.queryPermissionList(idList, parentId, keywords, permTypeList, visible
				, pageNo, pageSize);
	}

	@Override
	public SysPermission queryPermissionByNo(String permNo) {
		String key = String.format(CACHE_KEY_PERMISSION_BY_NO, getAppName(), permNo);
		SysPermission permission = cache.get(key);
		if (permission != null) {
			return permission;
		}

		permission = sysPermissionMapper.queryPermissionByNo(permNo);
		if (permission != null) {
			cache.put(key, permission, DEFAULT_CACHE_TIMEOUT);
		}
		return permission;
	}

	@Override
	public void deletePermissionByNoFromCache(String permNo) {
		String key = String.format(CACHE_KEY_PERMISSION_BY_NO, getAppName(), permNo);
		cache.remove(key);
	}

	@Override
	public int queryChildPermissionCount(long parentId) {
		return sysPermissionMapper.queryPermissionCount(null, parentId, null, null, null);
	}
}


