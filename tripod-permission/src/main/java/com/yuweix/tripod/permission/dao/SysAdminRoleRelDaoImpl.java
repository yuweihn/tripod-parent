package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.permission.dto.SysAdminRoleDto;
import com.yuweix.tripod.permission.mapper.SysAdminRoleRelMapper;
import com.yuweix.tripod.permission.mapper.SysRolePermissionRelMapper;
import com.yuweix.tripod.permission.model.SysAdminRoleRel;
import com.yuweix.tripod.permission.model.SysRolePermissionRel;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.CacheableDao;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.mybatis.where.Operator;
import com.yuweix.tripod.permission.common.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author yuwei
 */
@Repository("sysAdminRoleRelDao")
public class SysAdminRoleRelDaoImpl extends CacheableDao<SysAdminRoleRel, Long> implements SysAdminRoleRelDao, ApplicationContextAware {
	@Resource
	private SysAdminRoleRelMapper sysAdminRoleRelMapper;
	@Resource
	private SysRolePermissionRelMapper sysRolePermissionRelMapper;
	@Resource
	private Properties properties;
	
	private ApplicationContext ctx;
	
	
	private static final String CACHE_KEY_HAS_ROLE = "cache.%s.has.role.%s.%s";
	private static final String CACHE_KEY_ADMIN_ROLE_BY_ADMIN_ID_ROLE_ID = "cache.%s.admin.role.by.admin.%s.role.%s";
	
	
	
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	protected BaseMapper<SysAdminRoleRel, Long> getMapper() {
		return sysAdminRoleRelMapper;
	}

	@Override
	protected String getAppName() {
		return properties.getAppName();
	}

	@Override
	protected void onchange(SysAdminRoleRel t) {
		deleteHasRoleFromCache(t.getAdminId(), t.getRoleId());
		deleteByAdminIdAndRoleIdFromCache(t.getAdminId(), t.getRoleId());
		
		deleteHasPermissionFromCache(t);
	}
	
	private void deleteHasPermissionFromCache(SysAdminRoleRel t) {
		long adminId = t.getAdminId();
		long roleId = t.getRoleId();
		SysAdminDao adminDao = ctx.getBean(SysAdminDao.class);
		int pageNo = 1;
		for (;;) {
			List<SysRolePermissionRel> rprList = sysRolePermissionRelMapper.findPageList(
					Criteria.of("role_id", Operator.eq, roleId), pageNo++, 10, null, SysRolePermissionRel.class);
			if (rprList == null || rprList.size() <= 0) {
				break;
			}
			for (SysRolePermissionRel rpr: rprList) {
				adminDao.deleteHasPermissionFromCache(adminId, rpr.getPermId());
			}
		}
	}
	
	
	@Override
	public boolean hasRole(long adminId, long roleId) {
		String key = String.format(CACHE_KEY_HAS_ROLE, getAppName(), adminId, roleId);
		Boolean res = cache.get(key);
		if (res != null) {
			return res;
		}
		
		res = sysAdminRoleRelMapper.hasRole(adminId, roleId);
		cache.put(key, res, properties.getCacheTimeout());
		return res;
	}
	
	@Override
	public void deleteHasRoleFromCache(long adminId, long roleId) {
		String key = String.format(CACHE_KEY_HAS_ROLE, getAppName(), adminId, roleId);
		cache.remove(key);
	}

	@Override
	public int queryAdminRoleCountByAdminId(Long adminId, Long roleId, String keywords) {
		return sysAdminRoleRelMapper.queryAdminRoleCountByAdminId(adminId, roleId, keywords);
	}

	@Override
	public List<SysAdminRoleDto> queryAdminRoleListByAdminId(Long adminId, Long roleId, String keywords, int pageNo, int pageSize) {
		return sysAdminRoleRelMapper.queryAdminRoleListByAdminId(adminId, roleId, keywords, pageNo, pageSize);
	}

	@Override
	public SysAdminRoleRel queryByAdminIdAndRoleId(long adminId, long roleId) {
		String key = String.format(CACHE_KEY_ADMIN_ROLE_BY_ADMIN_ID_ROLE_ID, getAppName(), adminId, roleId);
		SysAdminRoleRel rel = cache.get(key);
		if (rel != null) {
			return rel;
		}
		
		rel = sysAdminRoleRelMapper.queryByAdminIdAndRoleId(adminId, roleId);
		if (rel != null) {
			cache.put(key, rel, properties.getCacheTimeout());
			return rel;
		} else {
			return null;
		}
	}
	
	@Override
	public void deleteByAdminIdAndRoleIdFromCache(long adminId, long roleId) {
		String key = String.format(CACHE_KEY_ADMIN_ROLE_BY_ADMIN_ID_ROLE_ID, getAppName(), adminId, roleId);
		cache.remove(key);
	}
}
