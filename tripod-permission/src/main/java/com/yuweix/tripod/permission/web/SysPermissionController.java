package com.yuweix.tripod.permission.web;


import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.permission.annotations.Permission;
import com.yuweix.tripod.permission.common.PermissionUtil;
import com.yuweix.tripod.permission.common.Properties;
import com.yuweix.tripod.permission.dto.AdminDto;
import com.yuweix.tripod.permission.dto.PermissionDto;
import com.yuweix.tripod.permission.service.SysPermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Sys权限管理
 * @author yuwei
 */
@Controller
public class SysPermissionController {
	@Resource
	private SysPermissionService sysPermissionService;
	@Resource
	private Properties properties;


	/**
	 * 权限列表
	 */
	@Permission(value = "sys.permission.list")
	@RequestMapping(value = "/sys/permission/list", method = GET)
	@ResponseBody
	public Response<String, List<PermissionDto>> queryPermissionList(@RequestParam(value = "keywords", required = false) String keywords
			, @RequestParam(value = "visible", required = false) Boolean visible) {
		List<PermissionDto> permissionList = sysPermissionService.queryPermissionListIncludeChildren(null, keywords
				, null, visible);
		return new Response<>(properties.getSuccessCode(), "ok", permissionList);
	}

	/**
	 * 添加权限
	 */
	@Permission(value = "sys.permission.create")
	@RequestMapping(value = "/sys/permission/create", method = POST)
	@ResponseBody
	public Response<String, Void> createPermission(@RequestParam(value = "permNo", required = true) String permNo
			, @RequestParam(value = "title", required = false) String title
			, @RequestParam(value = "parentId", required = false) Long parentId
			, @RequestParam(value = "orderNum", required = false, defaultValue = "0") int orderNum
			, @RequestParam(value = "path", required = false) String path
			, @RequestParam(value = "component", required = false) String component
			, @RequestParam(value = "ifExt", required = false, defaultValue = "false") boolean ifExt
			, @RequestParam(value = "permType", required = false) String permType
			, @RequestParam(value = "visible", required = false, defaultValue = "true") boolean visible
			, @RequestParam(value = "icon", required = false) String icon
			, @RequestParam(value = "descr", required = false) String descr) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		sysPermissionService.addPermission(permNo, title, parentId, orderNum, path, component, ifExt
				, permType, visible, icon, descr, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 修改权限
	 */
	@Permission(value = "sys.permission.update")
	@RequestMapping(value = "/sys/permission/update", method = POST)
	@ResponseBody
	public Response<String, Void> updatePermission(@RequestParam(value = "id", required = true) long id
			, @RequestParam(value = "permNo", required = true) String permNo
			, @RequestParam(value = "title", required = false) String title
			, @RequestParam(value = "parentId", required = false) Long parentId
			, @RequestParam(value = "orderNum", required = false, defaultValue = "0") int orderNum
			, @RequestParam(value = "path", required = false) String path
			, @RequestParam(value = "component", required = false) String component
			, @RequestParam(value = "ifExt", required = false, defaultValue = "false") boolean ifExt
			, @RequestParam(value = "permType", required = false) String permType
			, @RequestParam(value = "visible", required = false, defaultValue = "true") boolean visible
			, @RequestParam(value = "icon", required = false) String icon
			, @RequestParam(value = "descr", required = false) String descr) {
		AdminDto adminDto = PermissionUtil.getLoginAccount();
		sysPermissionService.updatePermission(id, permNo, title, parentId, orderNum, path, component, ifExt
				, permType, visible, icon, descr, adminDto.getAccountNo());
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 删除权限
	 */
	@Permission(value = "sys.permission.delete")
	@RequestMapping(value = "/sys/permission/delete", method = DELETE)
	@ResponseBody
	public Response<String, Void> deletePermission(@RequestParam(value = "ids", required = true)long[] ids) {
		for (long id: ids) {
			sysPermissionService.deletePermission(id);
		}
		return new Response<>(properties.getSuccessCode(), "ok");
	}
}
