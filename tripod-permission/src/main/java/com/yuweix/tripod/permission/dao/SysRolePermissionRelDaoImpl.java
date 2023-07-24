package com.yuweix.tripod.permission.dao;


import com.yuweix.tripod.permission.mapper.SysAdminRoleRelMapper;
import com.yuweix.tripod.permission.mapper.SysRolePermissionRelMapper;
import com.yuweix.tripod.permission.model.SysAdminRoleRel;
import com.yuweix.tripod.permission.model.SysRolePermissionRel;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.CacheableDao;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.mybatis.where.Operator;
import com.yuweix.tripod.permission.common.Properties;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author yuwei
 */
@Repository("sysRolePermissionRelDao")
public class SysRolePermissionRelDaoImpl extends CacheableDao<SysRolePermissionRel, Long> implements SysRolePermissionRelDao, ApplicationContextAware {
	@Resource
	private SysRolePermissionRelMapper sysRolePermissionRelMapper;
	@Resource
	private SysAdminRoleRelMapper sysAdminRoleRelMapper;
	@Resource
	private Properties properties;
	
	private ApplicationContext ctx;
	
	
	private static final String CACHE_KEY_RPR_BY_ROLE_PERM = "cache.%s.role.perm.rel.by.role.%s.perm.%s";
	
	
	
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	protected BaseMapper<SysRolePermissionRel, Long> getMapper() {
		return sysRolePermissionRelMapper;
	}

	@Override
	protected String getAppName() {
		return properties.getAppName();
	}
	
	@Override
	protected void onchange(SysRolePermissionRel t) {
		deleteByRoleIdAndPermIdFromCache(t.getRoleId(), t.getPermId());
		deleteHasPermissionFromCache(t);
	}
	
	private void deleteHasPermissionFromCache(SysRolePermissionRel t) {
		long permId = t.getPermId();
		long roleId = t.getRoleId();
		SysAdminDao adminDao = ctx.getBean(SysAdminDao.class);
		int pageNo = 1;
		for (;;) {
			List<SysAdminRoleRel> arrList = sysAdminRoleRelMapper.findPageList(
					Criteria.of("role_id", Operator.eq, roleId), pageNo++, 10, null, SysAdminRoleRel.class);
			if (arrList == null || arrList.size() <= 0) {
				break;
			}
			for (SysAdminRoleRel arr: arrList) {
				adminDao.deleteHasPermissionFromCache(arr.getAdminId(), permId);
			}
		}
	}


	@Override
	public List<SysRolePermissionRel> queryListByRoleId(long roleId) {
		return sysRolePermissionRelMapper.queryListByRoleId(roleId);
	}

	@Override
	public List<Long> queryPermIdListByRoleId(long roleId) {
		return sysRolePermissionRelMapper.queryPermIdListByRoleId(roleId);
	}

	@Override
	public SysRolePermissionRel queryByRoleIdAndPermId(long roleId, long permId) {
		String key = String.format(CACHE_KEY_RPR_BY_ROLE_PERM, getAppName(), roleId, permId);
		SysRolePermissionRel rel = cache.get(key);
		if (rel != null) {
			return rel;
		}
		
		rel = sysRolePermissionRelMapper.queryByRoleIdAndPermId(roleId, permId);
		if (rel != null) {
			cache.put(key, rel, properties.getCacheTimeout());
			return rel;
		} else {
			return null;
		}
	}
	
	@Override
	public void deleteByRoleIdAndPermIdFromCache(long roleId, long permId) {
		String key = String.format(CACHE_KEY_RPR_BY_ROLE_PERM, getAppName(), roleId, permId);
		cache.remove(key);
	}
}

