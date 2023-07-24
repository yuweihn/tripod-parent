package com.yuweix.tripod.permission.web;


import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.permission.annotations.Permission;
import com.yuweix.tripod.permission.common.PermissionUtil;
import com.yuweix.tripod.permission.common.Properties;
import com.yuweix.tripod.permission.dto.AdminDto;
import com.yuweix.tripod.permission.dto.PermissionDto;
import com.yuweix.tripod.permission.dto.RolePermissionDto;
import com.yuweix.tripod.permission.service.SysPermissionService;
import com.yuweix.tripod.permission.service.SysRolePermissionService;
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
	private SysPermissionService sysPermissionService;
	@Resource
	private SysRolePermissionService sysRolePermissionService;
	@Resource
	private Properties properties;


	/**
	 * 查询指定角色的权限集合
	 */
	@Permission(value = "sys.role.permission.list")
	@RequestMapping(value = "/sys/role/permission/list", method = GET)
	@ResponseBody
	public Response<String, RolePermissionDto> queryPermissionListByRoleId(@RequestParam(value = "roleId", required = true) long roleId) {
		List<PermissionDto> permissionList = sysPermissionService.queryPermissionListIncludeChildren(null
				, null, null, null);
		List<Long> permIdList = sysRolePermissionService.queryPermissionIdListByRoleId(roleId);

		RolePermissionDto dto = new RolePermissionDto();
		dto.setPermList(permissionList);
		dto.setCheckedPermIdList(permIdList);
		return new Response<>(properties.getSuccessCode(), "ok", dto);
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
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		sysRolePermissionService.saveRolePermission(roleId, permIdList, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}
}
