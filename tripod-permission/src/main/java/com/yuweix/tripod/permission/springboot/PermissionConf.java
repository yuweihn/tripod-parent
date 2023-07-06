package com.yuweix.tripod.permission.springboot;


import com.yuweix.tripod.core.json.Json;
import com.yuweix.tripod.permission.web.interceptor.PermissionCheckInterceptor;
import com.yuweix.tripod.sequence.base.SequenceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
	@Bean
	@ConfigurationProperties(prefix = "tripod.sequence", ignoreUnknownFields = true)
	public SequenceBean sequenceBean() {
		return new SequenceBean() {
			private Map<String, String> map = new HashMap<>();
			@Override
			public Map<String, String> getBeans() {
				return map;
			}
			@Override
			public Map<String, String> getBaseBeans() {
				Map<String, String> baseBeans = new HashMap<>();
				baseBeans.put("seqSysAdmin", "seq_sys_admin,200");
				baseBeans.put("seqSysPermission", "seq_sys_permission,200");
				baseBeans.put("seqSysRole", "seq_sys_role,200");
				baseBeans.put("seqSysRolePermissionRel", "seq_sys_role_permission_rel,200");
				baseBeans.put("seqSysAdminRoleRel", "seq_sys_admin_role_rel,200");
				return baseBeans;
			}
		};
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
