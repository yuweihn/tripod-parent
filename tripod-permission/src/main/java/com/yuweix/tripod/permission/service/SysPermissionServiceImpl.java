package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.core.DateUtil;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.mybatis.where.Operator;
import com.yuweix.tripod.permission.dao.SysAdminDao;
import com.yuweix.tripod.permission.dao.SysPermissionDao;
import com.yuweix.tripod.permission.dao.SysRolePermissionRelDao;
import com.yuweix.tripod.permission.dto.AbstractTreeDto;
import com.yuweix.tripod.permission.dto.PermissionDto;
import com.yuweix.tripod.permission.dto.PermissionExportDto;
import com.yuweix.tripod.permission.dto.PermissionMenuTreeDto;
import com.yuweix.tripod.permission.enums.PermType;
import com.yuweix.tripod.permission.model.SysPermission;
import com.yuweix.tripod.sequence.base.Sequence;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


/**
 * @author yuwei
 */
@Service("sysPermissionService")
public class SysPermissionServiceImpl implements SysPermissionService {
	@Resource
	private SysAdminDao sysAdminDao;
	@Resource
	private SysPermissionDao permissionDao;
	@Resource
	private SysRolePermissionRelDao sysRolePermissionRelDao;

	@Resource
	private Sequence seqSysPermission;


	@Override
	public List<PermissionDto> queryPermissionListIncludeChildren(List<Long> idList, String keywords, List<String> permTypeList
			, Boolean visible) {
		List<SysPermission> permissionList = permissionDao.queryPermissionList(idList, null, keywords, permTypeList, visible
				, null, null);
		List<PermissionDto> dotList = permissionList == null || permissionList.size() <= 0
				? new ArrayList<>()
				: permissionList.stream().map(this::toPermissionDto).collect(Collectors.toList());
		return buildPermTree(dotList);
	}
	private PermissionDto toPermissionDto(SysPermission permission) {
		if (permission == null) {
			return null;
		}
		PermissionDto dto = new PermissionDto();
		dto.setId(permission.getId());
		dto.setPermNo(permission.getPermNo());
		dto.setTitle(permission.getTitle());
		dto.setParentId(permission.getParentId());
		dto.setOrderNum(permission.getOrderNum());
		dto.setPath(permission.getPath());
		dto.setComponent(permission.getComponent());
		dto.setIfExt(permission.isIfExt());
		dto.setPermType(permission.getPermType());
		dto.setPermTypeName(PermType.getNameByCode(permission.getPermType()));
		dto.setVisible(permission.isVisible());
		dto.setIcon(permission.getIcon());
		dto.setDescr(permission.getDescr());
		dto.setCreator(permission.getCreator());
		dto.setCreateTime(permission.getCreateTime() == null ? "" : DateUtil.formatDate(permission.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		dto.setModifier(permission.getModifier());
		dto.setModifyTime(permission.getModifyTime() == null ? "" : DateUtil.formatDate(permission.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
		return dto;
	}
	/**
	 * 构建树状结构
	 */
	private <T extends AbstractTreeDto<T>>List<T> buildPermTree(List<T> dotList) {
		if (dotList == null || dotList.size() <= 0) {
			return dotList;
		}
		CopyOnWriteArrayList<T> copyList = new CopyOnWriteArrayList<>(dotList);
		List<Long> delIds = new ArrayList<>();
		for (T dto1 : copyList) {
			for (T dto2 : copyList) {
				if (dto1.getParentId() != null && dto1.getParentId() == dto2.getId()) {
					dto2.addChild(dto1);
					delIds.add(dto1.getId());
					break;
				}
			}
		}
		copyList.removeIf(dto1 -> delIds.contains(dto1.getId()));
		return copyList;
	}

	@Override
	public List<PermissionMenuTreeDto> getMenuTreeListByAdminId(long adminId) {
		List<Long> permIdList = sysAdminDao.queryPermissionIdListByAdminId(adminId);
		if (permIdList == null) {
			permIdList = new ArrayList<>();
		}
		List<String> permTypeList = new ArrayList<>();
		permTypeList.add(PermType.DIR.getCode());
		permTypeList.add(PermType.MENU.getCode());

		List<SysPermission> permissionList = permissionDao.queryPermissionList(permIdList, null, null
				, permTypeList, null, null, null);
		List<PermissionMenuTreeDto> dotList = permissionList == null || permissionList.size() <= 0
				? new ArrayList<>()
				: permissionList.stream().map(this::toPermissionMenuTreeDto).collect(Collectors.toList());
		return buildPermTree(dotList);
	}
	private PermissionMenuTreeDto toPermissionMenuTreeDto(SysPermission permission) {
		if (permission == null) {
			return null;
		}
		PermissionMenuTreeDto.Meta meta = new PermissionMenuTreeDto.Meta();
		meta.setTitle(permission.getTitle());
		meta.setIcon(permission.getIcon());
		PermissionMenuTreeDto dto = new PermissionMenuTreeDto();
		dto.setId(permission.getId());
		dto.setPath(permission.getPath() == null ? "" : permission.getPath());
		dto.setName(permission.getPermNo() == null ? "" : permission.getPermNo());
		dto.setPermType(permission.getPermType());
		dto.setComponent(permission.getComponent());
		dto.setIfExt(permission.isIfExt());
		dto.setHidden(!permission.isVisible());
		dto.setMeta(meta);
		dto.setParentId(permission.getParentId());
		return dto;
	}

	@Override
	public List<String> getPermissionNoListByAdminId(long adminId) {
		List<Long> permIdList = sysAdminDao.queryPermissionIdListByAdminId(adminId);
		if (permIdList == null) {
			permIdList = new ArrayList<>();
		}
		List<String> permTypeList = new ArrayList<>();
		permTypeList.add(PermType.BUTTON.getCode());

		List<SysPermission> permissionList = permissionDao.queryPermissionList(permIdList, null, null
				, permTypeList, true, null, null);
		return permissionList == null || permissionList.size() <= 0
				? new ArrayList<>()
				: permissionList.stream().map(SysPermission::getPermNo)
				.filter(permNo -> permNo != null && !"".equals(permNo))
				.collect(Collectors.toList());
	}

	@Override
	public PermissionDto queryPermissionById(long permissionId) {
		SysPermission permission = permissionDao.get(permissionId);
		return toPermissionDto(permission);
	}

	@Override
	public long addPermission(String permNo, String title, Long parentId, int orderNum, String path
			, String component, boolean ifExt, String permType, boolean visible
			, String icon, String descr, String creator) {
		SysPermission permission = permissionDao.queryPermissionByNo(permNo);
		if (permission != null) {
			throw new RuntimeException("权限编码已存在[" + permNo + "]");
		}

		long id = seqSysPermission.next();
		permission = new SysPermission();
		permission.setId(id);
		permission.setPermNo(permNo);
		permission.setTitle(title);
		permission.setParentId(parentId);
		permission.setOrderNum(orderNum);
		permission.setPath(path);
		permission.setComponent(component);
		permission.setIfExt(ifExt);
		permission.setPermType(permType);
		permission.setVisible(visible);
		permission.setIcon(icon);
		permission.setDescr(descr);
		permission.setCreator(creator);
		permission.setCreateTime(new Date());
		permissionDao.insertSelective(permission);
		return id;
	}

	@Override
	public void updatePermission(long id, String permNo, String title, Long parentId, int orderNum, String path
			, String component, boolean ifExt, String permType, boolean visible
			, String icon, String descr, String modifier) {
		SysPermission permission = permissionDao.get(id);
		if (permission == null) {
			throw new RuntimeException("该权限不存在[id=" + id + "]");
		}
		SysPermission permission2 = permissionDao.queryPermissionByNo(permNo);
		if (permission2 != null && permission2.getId() != id) {
			throw new RuntimeException("权限编码已存在[" + permNo + "]");
		}
		if (parentId != null && parentId == id) {
			throw new RuntimeException("上级权限不能选择自己");
		}

		permissionDao.deletePermissionByNoFromCache(permission.getPermNo());

		permission.setPermNo(permNo);
		permission.setTitle(title);
		permission.setParentId(parentId);
		permission.setOrderNum(orderNum);
		permission.setPath(path);
		permission.setComponent(component);
		permission.setIfExt(ifExt);
		permission.setPermType(permType);
		permission.setVisible(visible);
		permission.setIcon(icon);
		permission.setDescr(descr);
		permission.setModifier(modifier);
		permission.setModifyTime(new Date());
		permissionDao.updateByPrimaryKey(permission);
	}

	@Override
	public void deletePermission(long permissionId) {
		Criteria criteria = Criteria.of("perm_id", Operator.eq, permissionId);
		int cnt = sysRolePermissionRelDao.findCount(criteria);
		if (cnt > 0) {
			throw new RuntimeException("已绑定角色，不能删除");
		}

		int childCount = permissionDao.queryChildPermissionCount(permissionId);
		if (childCount > 0) {
			throw new RuntimeException("存在子权限，不能删除");
		}
		permissionDao.deleteByKey(permissionId);
	}

	@Override
	public void deleteAll() {
		List<SysPermission> list = permissionDao.queryPermissionList(null, null, null, null, null
				, null, null);
		if (list == null || list.size() <= 0) {
			return;
		}
		for (SysPermission perm: list) {
			permissionDao.delete(perm);
		}
	}

	@Override
	public PermissionDto queryPermissionByNo(String permNo) {
		SysPermission permission = permissionDao.queryPermissionByNo(permNo);
		return toPermissionDto(permission);
	}

	@Override
	public PermissionExportDto queryAllPermissionList() {
		List<SysPermission> list = permissionDao.queryPermissionList(null, null, null, null, null
				, null, null);
		return list == null || list.size() <= 0
				? null
				: toPermissionExportDto(list);
	}
	private PermissionExportDto toPermissionExportDto(List<SysPermission> plist) {
		if (plist == null || plist.size() <= 0) {
			return null;
		}
		PermissionExportDto dto = new PermissionExportDto();
		for (SysPermission perm: plist) {
			PermissionExportDto.Body body = new PermissionExportDto.Body();
			body.setId(perm.getId());
			body.setPermNo(perm.getPermNo());
			body.setTitle(perm.getTitle());
			body.setParentId(perm.getParentId());
			body.setOrderNum(perm.getOrderNum());
			body.setPath(perm.getPath());
			body.setComponent(perm.getComponent());
			body.setIfExt(perm.isIfExt());
			body.setPermType(perm.getPermType());
			body.setPermTypeName(PermType.getNameByCode(perm.getPermType()));
			body.setVisible(perm.isVisible());
			body.setIcon(perm.getIcon());
			body.setDescr(perm.getDescr());
			body.setCreator(perm.getCreator());
			body.setCreateTime(perm.getCreateTime() == null ? "" : DateUtil.formatDate(perm.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			body.setModifier(perm.getModifier());
			body.setModifyTime(perm.getModifyTime() == null ? "" : DateUtil.formatDate(perm.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
			dto.addBody(body);
		}
		return dto.sign();
	}

	/**
	 * 按 permNo 覆盖数据
	 */
	@Override
	public void doImport(String permNo, String title, Long parentId, Integer orderNum, String path, String component
			, Boolean ifExt, String permType, Boolean visible, String icon, String descr
			, String creator, String createTime, String modifier, String modifyTime) {
		if (permNo == null || "".equals(permNo.trim())) {
			return;
		}
		Date createTime0 = DateUtil.parseDateIgnoreE(createTime, "yyyy-MM-dd HH:mm:ss");
		Date modifyTime0 = DateUtil.parseDateIgnoreE(modifyTime, "yyyy-MM-dd HH:mm:ss");
		SysPermission perm = permissionDao.queryPermissionByNo(permNo.trim());
		if (perm == null) {
			perm = new SysPermission();
			perm.setId(seqSysPermission.next());
			perm.setPermNo(permNo.trim());
			perm.setTitle(title);
			perm.setParentId(parentId);
			perm.setOrderNum(orderNum == null ? 0 : orderNum);
			perm.setPath(path);
			perm.setComponent(component);
			perm.setIfExt(ifExt != null && ifExt);
			perm.setPermType(permType);
			perm.setVisible(visible != null && visible);
			perm.setIcon(icon);
			perm.setDescr(descr);
			perm.setCreator(creator);
			perm.setCreateTime(createTime0 == null ? new Date() : createTime0);
			perm.setModifier(modifier);
			perm.setModifyTime(modifyTime0);
			permissionDao.insert(perm);
		} else {
			perm.setTitle(title);
			perm.setParentId(parentId);
			perm.setOrderNum(orderNum == null ? 0 : orderNum);
			perm.setPath(path);
			perm.setComponent(component);
			perm.setIfExt(ifExt != null && ifExt);
			perm.setPermType(permType);
			perm.setVisible(visible != null && visible);
			perm.setIcon(icon);
			perm.setDescr(descr);
			perm.setModifier(modifier);
			perm.setModifyTime(modifyTime0);
			permissionDao.updateByPrimaryKey(perm);
		}
	}
}
