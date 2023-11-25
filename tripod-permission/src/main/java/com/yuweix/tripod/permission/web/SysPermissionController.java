package com.yuweix.tripod.permission.web;


import com.yuweix.tripod.core.DateUtil;
import com.yuweix.tripod.core.Response;
import com.yuweix.tripod.core.json.JsonUtil;
import com.yuweix.tripod.permission.annotations.Permission;
import com.yuweix.tripod.permission.common.PermissionUtil;
import com.yuweix.tripod.permission.common.Properties;
import com.yuweix.tripod.permission.dto.AdminDto;
import com.yuweix.tripod.permission.dto.PermissionDto;
import com.yuweix.tripod.permission.dto.PermissionExportDto;
import com.yuweix.tripod.permission.service.SysPermissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Sys权限管理
 * @author yuwei
 */
@Controller
public class SysPermissionController {
	private static final Logger log = LoggerFactory.getLogger(SysPermissionController.class);

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
		List<Long> list = new ArrayList<>();
		for (long id: ids) {
			list.add(id);
		}
		sysPermissionService.deletePermissions(list);
		return new Response<>(properties.getSuccessCode(), "ok");
	}

	/**
	 * 导出全部权限
	 */
	@Permission(value = "sys.permission.export")
	@RequestMapping(value = "/sys/permission/export", method = POST)
	@ResponseBody
	public void doExport(HttpServletResponse response) throws Exception {
		PermissionExportDto exportDto = sysPermissionService.getPermissionExportDto();

		String fileName = URLEncoder.encode("permission." + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss") + ".json", "utf-8");
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);
		response.setHeader("_filename", fileName);
		response.setHeader("Access-Control-Expose-Headers", "_filename");

		ServletOutputStream out = response.getOutputStream();
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			String str = exportDto == null ? "" : JsonUtil.toJSONString(exportDto);
			osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
			bw = new BufferedWriter(osw);
			bw.append(str);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	/**
	 * 导入外部权限数据
	 */
	@Permission(value = "sys.permission.import")
	@RequestMapping(value = "/sys/permission/import", method = POST)
	@ResponseBody
	public Response<String, Void> doImport(@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
		String str = new String(file.getBytes(), StandardCharsets.UTF_8);
		PermissionExportDto dto = "".equals(str)
				? null
				: JsonUtil.parseObject(str, PermissionExportDto.class);
		if (!dto.verify()) {
			return new Response<>(properties.getFailureCode(), "验签失败！");
		}
		List<PermissionDto> list = dto.getList();
		if (list == null || list.size() <= 0) {
			return new Response<>(properties.getFailureCode(), "No Data.");
		}
		sysPermissionService.doImport(null, list);
		return new Response<>(properties.getSuccessCode(), "ok");
	}
}
