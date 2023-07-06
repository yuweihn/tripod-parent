package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.core.BeanUtil;
import com.yuweix.tripod.permission.dao.SysRolePermissionRelDao;
import com.yuweix.tripod.permission.model.SysRolePermissionRel;
import com.yuweix.tripod.sequence.base.Sequence;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * @author yuwei
 */
@Service("rolePermissionService")
public class RolePermissionServiceImpl implements RolePermissionService {
	@Resource
	private SysRolePermissionRelDao sysRolePermissionRelDao;

	@Resource
	private Sequence seqSysRolePermissionRel;

	
	@Override
	public List<Long> queryPermissionIdListByRoleId(long roleId) {
		return sysRolePermissionRelDao.queryPermIdListByRoleId(roleId);
	}

	@Override
	public void saveRolePermission(long roleId, List<Long> permIdList, String modifier) {
		List<SysRolePermissionRel> relList = sysRolePermissionRelDao.queryListByRoleId(roleId);

		/**
		 * 之前已经分配的权限，如果仍在待分配的权限列表中，则保留，否则删除
		 */
		if (!CollectionUtils.isEmpty(relList)) {
			for (SysRolePermissionRel rel: relList) {
				boolean exists = false;
				if (!CollectionUtils.isEmpty(permIdList)) {
					exists = BeanUtil.exists(permIdList, rel.getPermId());
				}
				if (!exists) {
					sysRolePermissionRelDao.delete(rel);
				}
			}
		}

		/**
		 * 分配新权限；
		 */
		if (!CollectionUtils.isEmpty(permIdList)) {
			Date now = new Date();
			for (long permId: permIdList) {
				SysRolePermissionRel rel = sysRolePermissionRelDao.queryByRoleIdAndPermId(roleId, permId);
				if (rel != null) {
					continue;
				}

				long id = seqSysRolePermissionRel.next();
				SysRolePermissionRel rel0 = new SysRolePermissionRel();
				rel0.setId(id);
				rel0.setRoleId(roleId);
				rel0.setPermId(permId);
				rel0.setCreator(modifier);
				rel0.setCreateTime(now);
				sysRolePermissionRelDao.insertSelective(rel0);
			}
		}
	}
}
