package com.yuweix.tripod.permission.web;


import com.wei.ai.common.AiActionUtil;
import com.wei.ai.common.ResponseCode;
import com.wei.ai.common.annotations.Permission;
import com.wei.ai.dto.AdminDto;
import com.wei.ai.dto.PageResponseDto;
import com.wei.ai.dto.RoleDto;
import com.wei.ai.service.RoleService;
import com.yuweix.tripod.core.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Sys角色管理
 * @author yuwei
 */
@Controller
public class SysRoleController {
	@Resource
	private RoleService roleService;


	/**
	 * 角色列表
	 */
	@Permission(value = "sys.role.list")
	@RequestMapping(value = "/sys/role/list", method = GET)
	@ResponseBody
	public Response<String, PageResponseDto<RoleDto>> queryRoleList(@RequestParam(value = "keywords", required = false) String keywords
			, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo
			, @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		int count = roleService.queryRoleCount(keywords);
		List<RoleDto> roleList = roleService.queryRoleList(keywords, pageNo, pageSize);
		PageResponseDto<RoleDto> dto = PageResponseDto.<RoleDto>builder()
				.size(count)
				.list(roleList)
				.build();
		return new Response<>(ResponseCode.SUCCESS.getCode(), "ok", dto);
	}

	/**
	 * 查询角色详情
	 */
	@Permission(value = "sys.role.info")
	@RequestMapping(value = "/sys/role/info", method = GET)
	@ResponseBody
	public Response<String, RoleDto> queryRoleInfo(@RequestParam(value = "id", required = true) long id) {
		RoleDto role = roleService.queryRoleById(id);
		if (role == null) {
			return new Response<>(ResponseCode.FAILURE.getCode(), "无数据");
		} else {
			return new Response<>(ResponseCode.SUCCESS.getCode(), "ok", role);
		}
	}

	/**
	 * 添加角色
	 */
	@Permission(value = "sys.role.create")
	@RequestMapping(value = "/sys/role/create", method = POST)
	@ResponseBody
	public Response<String, Void> createRole(@RequestParam(value = "roleNo", required = true)String roleNo
			, @RequestParam(value = "roleName", required = true) String roleName) {
		AdminDto adminDto = AiActionUtil.getLoginAccount();
		roleService.addRole(roleNo.trim(), roleName, adminDto.getAccountNo());
		return new Response<>(ResponseCode.SUCCESS.getCode(), "ok");
	}

	/**
	 * 修改角色
	 */
	@Permission(value = "sys.role.update")
	@RequestMapping(value = "/sys/role/update", method = POST)
	@ResponseBody
	public Response<String, Void> updateRole(@RequestParam(value = "id", required = true) long id
			, @RequestParam(value = "roleNo", required = true)String roleNo
			, @RequestParam(value = "roleName", required = true) String roleName) {
		AdminDto adminDto = AiActionUtil.getLoginAccount();
		roleService.updateRole(id, roleNo.trim(), roleName, adminDto.getAccountNo());
		return new Response<>(ResponseCode.SUCCESS.getCode(), "ok");
	}

	/**
	 * 删除角色
	 */
	@Permission(value = "sys.role.delete")
	@RequestMapping(value = "/sys/role/delete", method = DELETE)
	@ResponseBody
	public Response<String, Void> deleteRole(@RequestParam(value = "ids", required = true)long[] ids) {
		for (long id: ids) {
			roleService.deleteRole(id);
		}
		return new Response<>(ResponseCode.SUCCESS.getCode(), "ok");
	}
}
