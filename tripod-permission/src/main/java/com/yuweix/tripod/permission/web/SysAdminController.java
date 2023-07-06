package com.yuweix.tripod.permission.web;


import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.permission.annotations.Permission;
import com.yuweix.tripod.permission.common.PermissionUtil;
import com.yuweix.tripod.permission.common.Properties;
import com.yuweix.tripod.permission.dto.AdminDto;
import com.yuweix.tripod.permission.dto.DropDownDto;
import com.yuweix.tripod.permission.dto.PageResponseDto;
import com.yuweix.tripod.permission.enums.EnumGender;
import com.yuweix.tripod.permission.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Sys管理员账户
 * @author yuwei
 */
@Controller
public class SysAdminController {
	@Resource
	private AdminService adminService;
	@Resource
	private Properties properties;


	/**
	 * 管理员列表
	 */
	@Permission(value = "sys.admin.list")
	@RequestMapping(value = "/sys/admin/list", method = GET)
	@ResponseBody
	public Response<String, PageResponseDto<AdminDto>> queryAdminList(
			@RequestParam(value = "keywords", required = false) String keywords
			, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo
			, @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		int size = adminService.queryAdminCount(keywords);
		List<AdminDto> adminList = adminService.queryAdminList(keywords, pageNo, pageSize);
		
		PageResponseDto<AdminDto> dto = new PageResponseDto<>();
		dto.setSize(size);
		dto.setList(adminList);
		return new Response<>(properties.getSuccessCode(), "ok", dto);
	}

	/**
	 * 查询管理员
	 */
	@Permission(value = "sys.admin.info")
	@RequestMapping(value = "/sys/admin/info", method = GET)
	@ResponseBody
	public Response<String, AdminDto> findAdminById(@RequestParam(value = "id", required = true) long accountId) {
		AdminDto adminDto = adminService.findAdminById(accountId);
		if (adminDto != null) {
			adminDto.setPassword(null);
		}
		return new Response<>(properties.getSuccessCode(), "ok", adminDto);
	}

	/**
	 * 添加管理员
	 */
	@Permission(value = "sys.admin.create")
	@RequestMapping(value = "/sys/admin/create", method = POST)
	@ResponseBody
	public Response<String, Void> createAdmin(@RequestParam(value = "accountNo", required = true) String accountNo
			, @RequestParam(value = "password", required = true) String password
			, @RequestParam(value = "realName", required = false) String realName
			, @RequestParam(value = "gender", required = false) Byte gender) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		adminService.createAccount(accountNo, password, realName, gender, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 更新管理员资料
	 */
	@Permission(value = "sys.admin.update")
	@RequestMapping(value = "/sys/admin/update", method = POST)
	@ResponseBody
	public Response<String, Void> updateAdmin(@RequestParam(value = "accountId", required = true) long accountId
			, @RequestParam(value = "realName", required = false) String realName
			, @RequestParam(value = "gender", required = false) Byte gender) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		adminService.updateAccount(accountId, realName, gender, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 设置密码
	 */
	@Permission(value = "sys.admin.change.password")
	@RequestMapping(value = "/sys/admin/change-password", method = POST)
	@ResponseBody
	public Response<String, Void> changePassword(@RequestParam(value = "accountId", required = true) long accountId
			, @RequestParam(value = "password", required = true) String password) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		adminService.changePassword(accountId, password, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 性别列表(下拉选择)
	 */
	@Permission(value = "sys.admin.gender.drop.down.list")
	@RequestMapping(value = "/sys/admin/gender/drop-down-list", method = GET)
	@ResponseBody
	public Response<String, List<DropDownDto>> queryGenderDropDownList() {
		EnumGender[] genders = EnumGender.values();
		List<DropDownDto> list = new ArrayList<>();
		if (genders != null && genders.length > 0) {
			for (EnumGender gender: genders) {
				DropDownDto dto = new DropDownDto();
				dto.setId(gender.getCode());
				dto.setName(gender.getName());
				list.add(dto);
			}
		}
		return new Response<>(properties.getSuccessCode(), "ok", list);
	}

	/**
	 * 删除管理员
	 */
	@Permission(value = "sys.admin.delete")
	@RequestMapping(value = "/sys/admin/delete", method = DELETE)
	@ResponseBody
	public Response<String, Void> deleteAdmin(@RequestParam(value = "ids", required = true) long[] ids) {
		for (long accountId: ids) {
			adminService.deleteAccount(accountId);
		}
		return new Response<>(properties.getSuccessCode(), "ok");
	}
}




