package com.yuweix.tripod.permission.web;


import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.permission.annotations.Permission;
import com.yuweix.tripod.permission.common.PermissionUtil;
import com.yuweix.tripod.permission.common.Properties;
import com.yuweix.tripod.permission.dto.AdminDto;
import com.yuweix.tripod.permission.dto.AdminRoleDto;
import com.yuweix.tripod.permission.dto.PageResponseDto;
import com.yuweix.tripod.permission.service.AdminRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Sys管理员角色管理
 * @author yuwei
 */
@Controller
public class SysAdminRoleController {
	@Resource
	private AdminRoleService adminRoleService;
	@Resource
	private Properties properties;


	/**
	 * 管理员角色列表
	 */
	@Permission(value = "sys.admin.role.list")
	@RequestMapping(value = "/sys/admin/role/list", method = GET)
	@ResponseBody
	public Response<String, PageResponseDto<AdminRoleDto>> queryAdminRoleList(@RequestParam(value = "adminId", required = true) long adminId
			, @RequestParam(value = "keywords", required = false) String keywords
			, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo
			, @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		int count = adminRoleService.queryAdminRoleCountByAdminId(adminId, keywords);
		List<AdminRoleDto> roleList = adminRoleService.queryAdminRoleListByAdminId(adminId, keywords, pageNo, pageSize);
		PageResponseDto<AdminRoleDto> dto = new PageResponseDto<>();
		dto.setSize(count);
		dto.setList(roleList);
		return new Response<>(properties.getSuccessCode(), "ok", dto);
	}

	/**
	 * 查询指定的管理员角色
	 */
	@Permission(value = "sys.admin.role.info")
	@RequestMapping(value = "/sys/admin/role/info", method = GET)
	@ResponseBody
	public Response<String, AdminRoleDto> queryAdminRoleInfo(@RequestParam(value = "id", required = true) long id) {
		AdminRoleDto dto = adminRoleService.queryAdminRoleById(id);
		return new Response<>(properties.getSuccessCode(), "ok", dto);
	}

	/**
	 * 增加管理员角色
	 */
	@Permission(value = "sys.admin.role.create")
	@RequestMapping(value = "/sys/admin/role/add", method = POST)
	@ResponseBody
	public Response<String, Long> addAdminRole(@RequestParam(value = "adminId", required = true) long adminId
			, @RequestParam(value = "roleId", required = true) long roleId) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		long id = adminRoleService.addAdminRole(adminId, roleId, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok", id);
	}

	/**
	 * 修改管理员角色
	 */
	@Permission(value = "sys.admin.role.update")
	@RequestMapping(value = "/sys/admin/role/update", method = POST)
	@ResponseBody
	public Response<String, Void> updateAdminRole(@RequestParam(value = "id", required = true) long id
			, @RequestParam(value = "adminId", required = true) long adminId
			, @RequestParam(value = "roleId", required = true) long roleId) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		adminRoleService.updateAdminRole(id, adminId, roleId, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 删除指定的管理员角色
	 */
	@Permission(value = "sys.admin.role.delete")
	@RequestMapping(value = "/sys/admin/role/delete", method = DELETE)
	@ResponseBody
	public Response<String, Void> deleteAdminRole(@RequestParam(value = "ids", required = true)long[] ids) {
		for (long id: ids) {
			adminRoleService.deleteAdminRole(id);
		}
		return new Response<>(properties.getSuccessCode(), "ok");
	}
}


