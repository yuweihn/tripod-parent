package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.core.BeanUtil;
import com.yuweix.tripod.core.DateUtil;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.mybatis.where.Operator;
import com.yuweix.tripod.permission.dao.SysAdminRoleRelDao;
import com.yuweix.tripod.permission.dao.SysRoleDao;
import com.yuweix.tripod.permission.dto.AdminRoleDto;
import com.yuweix.tripod.permission.dto.SysAdminRoleDto;
import com.yuweix.tripod.permission.model.SysAdminRoleRel;
import com.yuweix.tripod.permission.model.SysRole;
import com.yuweix.tripod.sequence.base.Sequence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author yuwei
 */
@Service("adminRoleService")
public class AdminRoleServiceImpl implements AdminRoleService {
	@Resource
	private SysAdminRoleRelDao sysAdminRoleRelDao;
	@Resource
	private SysRoleDao sysRoleDao;

	@Resource
	private Sequence seqSysAdminRoleRel;


	@Override
	public int queryAdminRoleCountByAdminId(long adminId, String keywords) {
		return sysAdminRoleRelDao.queryAdminRoleCountByAdminId(adminId, null, keywords);
	}

	@Override
	public List<AdminRoleDto> queryAdminRoleListByAdminId(long adminId, String keywords, int pageNo, int pageSize) {
		List<AdminRoleDto> dtoList = new ArrayList<>();
		List<SysAdminRoleDto> arDtoList = sysAdminRoleRelDao.queryAdminRoleListByAdminId(adminId, null, keywords, pageNo, pageSize);

		if (CollectionUtils.isEmpty(arDtoList)) {
			return dtoList;
		}

		for (SysAdminRoleDto arDto: arDtoList) {
			AdminRoleDto dto = new AdminRoleDto();
			dto.setId(arDto.getId());
			dto.setAdminId(arDto.getAdminId());
			dto.setRoleId(arDto.getRoleId());
			dto.setRoleNo(arDto.getRoleNo());
			dto.setRoleName(arDto.getRoleName());
			dto.setCreator(arDto.getCreator());
			dto.setCreateTime(arDto.getCreateTime() == null ? "" : DateUtil.formatDate(arDto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			dto.setModifier(arDto.getModifier());
			dto.setModifyTime(arDto.getModifyTime() == null ? "" : DateUtil.formatDate(arDto.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	@Transactional
	public void addAdminRoleList(long adminId, List<Long> roleIdList, String modifier) {
		Criteria criteria = Criteria.of("admin_id", Operator.eq, adminId);
		List<SysAdminRoleRel> relList = sysAdminRoleRelDao.findList(criteria, null);

		/**
		 * 之前已经分配的角色，如果仍在待分配的角色列表中，则保留，否则删除
		 */
		if (!CollectionUtils.isEmpty(relList)) {
			for (SysAdminRoleRel rel: relList) {
				boolean exists = false;
				if (!CollectionUtils.isEmpty(roleIdList)) {
					exists = BeanUtil.exists(roleIdList, rel.getRoleId());
				}
				if (!exists) {
					sysAdminRoleRelDao.delete(rel);
				}
			}
		}

		/**
		 * 分配新角色；
		 */
		if (!CollectionUtils.isEmpty(roleIdList)) {
			Date now = new Date();
			for (long roleId: roleIdList) {
				boolean hasThisRole = sysAdminRoleRelDao.hasRole(adminId, roleId);
				if (hasThisRole) {
					continue;
				}

				long id = seqSysAdminRoleRel.next();
				SysAdminRoleRel sarr = new SysAdminRoleRel();
				sarr.setId(id);
				sarr.setAdminId(adminId);
				sarr.setRoleId(roleId);
				sarr.setCreator(modifier);
				sarr.setCreateTime(now);
				sysAdminRoleRelDao.insertSelective(sarr);
			}
		}
	}

	@Override
	@Transactional
	public long addAdminRole(long adminId, long roleId, String creator) {
		SysAdminRoleRel rel = sysAdminRoleRelDao.queryByAdminIdAndRoleId(adminId, roleId);
		if (rel != null) {
			throw new RuntimeException("数据已存在");
		}

		long id = seqSysAdminRoleRel.next();
		rel = new SysAdminRoleRel();
		rel.setId(id);
		rel.setAdminId(adminId);
		rel.setRoleId(roleId);
		rel.setCreator(creator);
		rel.setCreateTime(new Date());
		sysAdminRoleRelDao.insertSelective(rel);
		return id;
	}

	@Override
	public AdminRoleDto queryAdminRoleById(long id) {
		SysAdminRoleRel rel = sysAdminRoleRelDao.get(id);
		if (rel == null) {
			return null;
		}
		SysRole role = sysRoleDao.get(rel.getRoleId());

		AdminRoleDto dto = new AdminRoleDto();
		dto.setId(rel.getId());
		dto.setAdminId(rel.getAdminId());
		dto.setRoleId(rel.getRoleId());
		dto.setRoleNo(role == null ? "" : role.getRoleNo());
		dto.setRoleName(role == null ? "" : role.getRoleName());
		dto.setCreator(rel.getCreator());
		dto.setCreateTime(rel.getCreateTime() == null ? "" : DateUtil.formatDate(rel.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		dto.setModifier(rel.getModifier());
		dto.setModifyTime(rel.getModifyTime() == null ? "" : DateUtil.formatDate(rel.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
		return dto;
	}

	@Override
	@Transactional
	public void updateAdminRole(long id, long adminId, long roleId, String modifier) {
		SysAdminRoleRel rel = sysAdminRoleRelDao.get(id);
		if (rel == null) {
			throw new RuntimeException("该管理员角色[id=" + id + "]不存在");
		}
		SysAdminRoleRel rel1 = sysAdminRoleRelDao.queryByAdminIdAndRoleId(adminId, roleId);
		if (rel1 != null && rel1.getId() != id) {
			throw new RuntimeException("该管理员角色[adminId=" + adminId + ", roleId=" + roleId + "]已存在");
		}

		rel.setAdminId(adminId);
		rel.setRoleId(roleId);
		rel.setModifier(modifier);
		rel.setModifyTime(new Date());
		sysAdminRoleRelDao.updateByPrimaryKeySelective(rel);
	}

	@Override
	@Transactional
	public void deleteAdminRole(long id) {
		sysAdminRoleRelDao.deleteByKey(id);
	}

	@Override
	public boolean hasRole(long adminId, long roleId) {
		return sysAdminRoleRelDao.hasRole(adminId, roleId);
	}
}
