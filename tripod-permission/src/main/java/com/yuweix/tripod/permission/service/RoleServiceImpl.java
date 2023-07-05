package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.core.DateUtil;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.mybatis.where.Operator;
import com.yuweix.tripod.permission.dao.SysAdminRoleRelDao;
import com.yuweix.tripod.permission.dao.SysRoleDao;
import com.yuweix.tripod.permission.dao.SysRolePermissionRelDao;
import com.yuweix.tripod.permission.dto.RoleDto;
import com.yuweix.tripod.permission.model.SysRole;
import com.yuweix.tripod.sequence.base.Sequence;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author yuwei
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
	@Resource
	private SysRoleDao sysRoleDao;
	@Resource
	private SysRolePermissionRelDao sysRolePermissionRelDao;
	@Resource
	private SysAdminRoleRelDao sysAdminRoleRelDao;

	@Resource
	private Sequence seqSysRole;


	@Override
	public List<RoleDto> queryAllRoleList() {
		List<SysRole> roleList = sysRoleDao.findList(null, null);
		return roleList == null || roleList.size() <= 0
				? new ArrayList<>()
				: roleList.stream().map(this::toRoleDto).collect(Collectors.toList());
	}
	private RoleDto toRoleDto(SysRole role) {
		if (role == null) {
			return null;
		}
		RoleDto dto = new RoleDto();
		dto.setId(role.getId());
		dto.setRoleNo(role.getRoleNo());
		dto.setRoleName(role.getRoleName());
		dto.setCreator(role.getCreator());
		dto.setCreateTime(role.getCreateTime() == null ? "" : DateUtil.formatDate(role.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		dto.setModifier(role.getModifier());
		dto.setModifyTime(role.getModifyTime() == null ? "" : DateUtil.formatDate(role.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
		return dto;
	}

	@Transactional
	@Override
	public long addRole(String roleNo, String roleName, String creator) {
		if (!StringUtils.hasText(roleName)) {
			throw new RuntimeException("角色名不能为空");
		}
		SysRole role0 = sysRoleDao.queryRoleByNo(roleNo);
		if (role0 != null) {
			throw new RuntimeException("角色编码已存在[" + roleNo + "]");
		}

		long id = seqSysRole.next();
		SysRole role = new SysRole();
		role.setId(id);
		role.setRoleNo(roleNo);
		role.setRoleName(roleName);
		role.setCreator(creator);
		role.setCreateTime(new Date());
		sysRoleDao.insertSelective(role);
		return id;
	}

	@Override
	public int queryRoleCount(String keywords) {
		Criteria criteria = Criteria.of("1", Operator.eq, 1);
		if (keywords != null && !"".equals(keywords.trim())) {
			criteria = Criteria.of("role_no", Operator.like, "%" + keywords + "%")
					.or("role_name", Operator.like, "%" + keywords + "%");
		}
		return sysRoleDao.findCount(criteria);
	}

	@Override
	public List<RoleDto> queryRoleList(String keywords, int pageNo, int pageSize) {
		Criteria criteria = Criteria.of("1", Operator.eq, 1);
		if (keywords != null && !"".equals(keywords.trim())) {
			criteria = Criteria.of("role_no", Operator.like, "%" + keywords + "%")
					.or("role_name", Operator.like, "%" + keywords + "%");
		}
		List<SysRole> roleList = sysRoleDao.findPageList(criteria, pageNo, pageSize, null);
		return roleList == null || roleList.size() <= 0
				? new ArrayList<>()
				: roleList.stream().map(this::toRoleDto).collect(Collectors.toList());
	}

	@Override
	public Long queryIdByRoleNo(String roleNo) {
		SysRole role = sysRoleDao.queryRoleByNo(roleNo);
		return role == null ? null : role.getId();
	}

	@Override
	public RoleDto queryRoleById(long roleId) {
		SysRole role = sysRoleDao.get(roleId);
		return toRoleDto(role);
	}

	@Transactional
	@Override
	public void updateRole(long id, String roleNo, String roleName, String modifier) {
		if (!StringUtils.hasText(roleName)) {
			throw new RuntimeException("角色名不能为空");
		}

		SysRole role = sysRoleDao.get(id);
		if (role == null) {
			throw new RuntimeException("角色名[id=" + id + "]不存在");
		}
		SysRole role0 = sysRoleDao.queryRoleByNo(roleNo);
		if (role0 != null && role0.getId() != id) {
			throw new RuntimeException("角色编码已存在[" + roleNo + "]");
		}

		role.setRoleNo(roleNo);
		role.setRoleName(roleName);
		role.setModifier(modifier);
		role.setModifyTime(new Date());
		sysRoleDao.updateByPrimaryKeySelective(role);
	}

	@Transactional
	@Override
	public void deleteRole(long roleId) {
		int cnt = sysRolePermissionRelDao.findCount(Criteria.of("role_id", Operator.eq, roleId));
		if (cnt > 0) {
			throw new RuntimeException("已绑定权限，不能删除");
		}

		int cnt2 = sysAdminRoleRelDao.findCount(Criteria.of("role_id", Operator.eq, roleId));
		if (cnt2 > 0) {
			throw new RuntimeException("已绑定管理员，不能删除");
		}
		sysRoleDao.deleteByKey(roleId);
	}
}
