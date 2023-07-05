package com.yuweix.tripod.permission.service;


import com.yuweix.tripod.core.ActionUtil;
import com.yuweix.tripod.core.DateUtil;
import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.dao.mybatis.order.OrderBy;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.mybatis.where.Operator;
import com.yuweix.tripod.permission.dao.SysAdminDao;
import com.yuweix.tripod.permission.dao.SysAdminRoleRelDao;
import com.yuweix.tripod.permission.dto.AdminDto;
import com.yuweix.tripod.permission.enums.EnumGender;
import com.yuweix.tripod.permission.model.SysAdmin;
import com.yuweix.tripod.sequence.base.Sequence;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author yuwei
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {
	@Resource
	private Sequence seqSysAdmin;
	
	@Resource
	private SysAdminDao sysAdminDao;
	@Resource
	private SysAdminRoleRelDao sysAdminRoleRelDao;

	
	@Transactional
	@Override
	public long createAccount(String accountNo, String password, String realName, Byte gender, String creator) {
		SysAdmin admin = sysAdminDao.findAdminByAccountNo(accountNo);
		if (admin != null) {
			throw new RuntimeException("账号[accountNo=" + accountNo + "]已存在");
		}
		admin = new SysAdmin();
		admin.setId(seqSysAdmin.next());
		admin.setAccountNo(accountNo);
		admin.setPassword(password);
		admin.setRealName(realName);
		admin.setGender(gender);
		admin.setCreator(creator);
		admin.setCreateTime(new Date());
		sysAdminDao.insert(admin);
		return admin.getId();
	}
	
	@Transactional
	@Override
	public void updateAccount(long id, String realName, Byte gender, String modifier) {
		SysAdmin admin = sysAdminDao.get(id);
		if (admin == null) {
			throw new RuntimeException("账号[id=" + id + "]不存在");
		}
		sysAdminDao.deleteAdminFromCache(admin.getAccountNo());

		admin.setRealName(realName);
		admin.setGender(gender);
		admin.setModifier(modifier);
		admin.setModifyTime(new Date());
		sysAdminDao.updateByPrimaryKey(admin);
	}
	
	@Override
	public AdminDto findAdminById(long id) {
		SysAdmin admin = sysAdminDao.get(id);
		return toAdminDto(admin);
	}
	private AdminDto toAdminDto(SysAdmin admin) {
		if (admin == null) {
			return null;
		}
		AdminDto dto = new AdminDto();
		dto.setId(admin.getId());
		dto.setAccountNo(admin.getAccountNo());
		dto.setPassword(admin.getPassword());
		dto.setRealName(admin.getRealName());
		dto.setGender(admin.getGender());
		dto.setGenderName(admin.getGender() == null ? "" : EnumGender.getNameByCode(admin.getGender()));
		dto.setAvatar(admin.getAvatar());
		dto.setLastLoginTime(admin.getLastLoginTime() == null ? "" : DateUtil.formatDate(admin.getLastLoginTime(), "yyyy-MM-dd HH:mm:ss"));
		dto.setLastLoginIp(admin.getLastLoginIp());
		dto.setCreator(admin.getCreator());
		dto.setCreateTime(admin.getCreateTime() == null ? "" : DateUtil.formatDate(admin.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		dto.setModifier(admin.getModifier());
		dto.setModifyTime(admin.getModifyTime() == null ? "" : DateUtil.formatDate(admin.getModifyTime(), "yyyy-MM-dd HH:mm:ss"));
		return dto;
	}
	
	@Override
	public Response<Boolean, AdminDto> login(String accountNo, String password) {
		SysAdmin admin = sysAdminDao.findAdminByAccountNo(accountNo);
		if (admin == null) {
			return new Response<>(false, "账号不存在[" + accountNo + "]");
		}
		if (!password.equals(admin.getPassword())) {
			return new Response<>(false, "密码错误");
		}
		admin.setLastLoginTime(new Date());
		admin.setLastLoginIp(ActionUtil.getRequestIP());
		sysAdminDao.updateByPrimaryKey(admin);

		return new Response<>(true, "登录成功", toAdminDto(admin));
	}

	@Transactional
	@Override
	public void changePassword(long id, String password, String modifier) {
		SysAdmin admin = sysAdminDao.get(id);
		if (admin == null) {
			throw new RuntimeException("账号[id=" + id + "]不存在");
		}
		admin.setPassword(password);
		admin.setModifier(modifier);
		admin.setModifyTime(new Date());
		sysAdminDao.updateByPrimaryKey(admin);
	}

	@Transactional
	@Override
	public void changePassword(long id, String oldPassword, String password, String modifier) {
		SysAdmin admin = sysAdminDao.get(id);
		if (admin == null) {
			throw new RuntimeException("账号[id=" + id + "]不存在");
		}
		if (!oldPassword.equals(admin.getPassword())) {
			throw new RuntimeException("原密码错误");
		}
		admin.setPassword(password);
		admin.setModifier(modifier);
		admin.setModifyTime(new Date());
		sysAdminDao.updateByPrimaryKey(admin);
	}
	
	@Transactional
	@Override
	public void changeAvatar(long id, String avatar, String modifier) {
		SysAdmin admin = sysAdminDao.get(id);
		if (admin == null) {
			throw new RuntimeException("账号[id=" + id + "]不存在");
		}
		admin.setAvatar(avatar);
		admin.setModifier(modifier);
		admin.setModifyTime(new Date());
		sysAdminDao.updateByPrimaryKey(admin);
	}
	
	@Transactional
	@Override
	public void deleteAccount(long id) {
		int cnt = sysAdminRoleRelDao.findCount(Criteria.of("admin_id", Operator.eq, id));
		if (cnt > 0) {
			throw new RuntimeException("已绑定角色，不能删除");
		}
		sysAdminDao.deleteByKey(id);
	}

	@Override
	public int queryAdminCount(String keywords) {
		Criteria criteria = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			criteria = Criteria.of("account_no", Operator.like, "%" + keywords.trim() + "%");
			criteria.or("real_name", Operator.like, "%" + keywords.trim() + "%");
		}
		return sysAdminDao.findCount(criteria);
	}

	@Override
	public List<AdminDto> queryAdminList(String keywords, int pageNo, int pageSize) {
		Criteria criteria = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			criteria = Criteria.of("account_no", Operator.like, "%" + keywords.trim() + "%");
			criteria.or("real_name", Operator.like, "%" + keywords.trim() + "%");
		}
		List<SysAdmin> list = sysAdminDao.findPageList(criteria, pageNo, pageSize, OrderBy.create("id"));
		return list == null || list.size() <= 0
				? new ArrayList<>()
				: list.stream().map(admin -> {
					AdminDto dto = this.toAdminDto(admin);
					dto.setPassword(null);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public boolean hasPermission(long adminId, long permissionId) {
		return sysAdminDao.hasPermission(adminId, permissionId);
	}

	@Override
	public List<Long> queryPermissionIdListByAdminId(long adminId) {
		return sysAdminDao.queryPermissionIdListByAdminId(adminId);
	}
}
