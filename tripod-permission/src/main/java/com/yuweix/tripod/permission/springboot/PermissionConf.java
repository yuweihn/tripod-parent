package com.yuweix.tripod.permission.springboot;


import com.yuweix.tripod.core.json.Json;
import com.yuweix.tripod.permission.web.interceptor.PermissionCheckInterceptor;
import com.yuweix.tripod.sequence.base.SequenceBeanProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
@ComponentScan(basePackages = "com.yuweix.tripod.permission", useDefaultFilters = true)
public class PermissionConf {
	@Bean(name = "sequenceBeanProcessor#advice")
	public Object sequenceBeanProcessorAdvice(@Autowired(required = false) SequenceBeanProcessor sequenceBeanProcessor) {
		if (sequenceBeanProcessor == null) {
			return null;
		}
		Map<String, String> beans = new HashMap<>();
		beans.put("seqSysAdmin", "seq_sys_admin,200");
		beans.put("seqSysPermission", "seq_sys_permission,200");
		beans.put("seqSysRole", "seq_sys_role,200");
		beans.put("seqSysRolePermissionRel", "seq_sys_role_permission_rel,200");
		beans.put("seqSysAdminRoleRel", "seq_sys_admin_role_rel,200");
		sequenceBeanProcessor.registerBeans(beans);
		return null;
	}

	@Bean(name = "json#advice")
	public Object jsonAdvice(@Autowired(required = false) Json json) {
		if (json == null) {
			return null;
		}
		json.addAccept("com.yuweix.tripod.permission.dto");
		json.addAccept("com.yuweix.tripod.permission.model");
		return null;
	}

	@Bean(name = "basePackage")
	public String basePackage(Environment env) {
		String str = env.getProperty("tripod.mybatis.base-package");
		String str0 = "com.yuweix.tripod.permission.mapper**";
		if (str == null) {
			return str0;
		} else {
			return str0 + "," + str;
		}
	}

	@ConditionalOnMissingBean(PermissionCheckInterceptor.class)
	@Bean
	public PermissionCheckInterceptor permissionCheckInterceptor() {
		return new PermissionCheckInterceptor();
	}
}
