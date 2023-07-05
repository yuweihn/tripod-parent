package com.yuweix.tripod.permission.web;


import com.wei.ai.common.AiActionUtil;
import com.wei.ai.common.ResponseCode;
import com.wei.ai.common.annotations.Permission;
import com.wei.ai.dto.AdminDto;
import com.wei.ai.dto.PermissionDto;
import com.wei.ai.dto.RolePermissionDto;
import com.wei.ai.service.PermissionService;
import com.wei.ai.service.RolePermissionService;
import com.yuweix.tripod.core.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * Sys角色权限管理
 * @author yuwei
 */
@Controller
public class SysRolePermissionController {
	@Resource
	private PermissionService permissionService;
	@Resource
	private RolePermissionService rolePermissionService;


	/**
	 * 查询指定角色的权限集合
	 */
	@Permission(value = "sys.role.permission.list")
	@RequestMapping(value = "/sys/role/permission/list", method = GET)
	@ResponseBody
	public Response<String, RolePermissionDto> queryPermissionListByRoleId(@RequestParam(value = "roleId", required = true) long roleId) {
		List<PermissionDto> permissionList = permissionService.queryPermissionListIncludeChildren(null
				, null, null, null);
		List<Long> permIdList = rolePermissionService.queryPermissionIdListByRoleId(roleId);

		RolePermissionDto dto = RolePermissionDto.builder()
				.permList(permissionList)
				.checkedPermIdList(permIdList)
				.build();
		return new Response<>(ResponseCode.SUCCESS.getCode(), "ok", dto);
	}

	/**
	 * 保存角色权限
	 */
	@Permission(value = "sys.role.permission.save")
	@RequestMapping(value = "/sys/role/permission/save", method = POST)
	@ResponseBody
	public Response<String, Void> saveRolePermission(@RequestParam(value = "roleId", required = true) long roleId
			, @RequestParam(value = "permIds", required = true)long[] permIds) {
		List<Long> permIdList = new ArrayList<>();
		if (permIds != null && permIds.length > 0) {
			for (long permId : permIds) {
				permIdList.add(permId);
			}
		}
		AdminDto adminDto = AiActionUtil.getLoginAccount();
		rolePermissionService.saveRolePermission(roleId, permIdList, adminDto.getAccountNo());
		return new Response<>(ResponseCode.SUCCESS.getCode(), "ok");
	}
}
