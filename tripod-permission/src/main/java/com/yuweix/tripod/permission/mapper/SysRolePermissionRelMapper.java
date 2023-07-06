package com.yuweix.tripod.permission.mapper;


import com.yuweix.tripod.permission.model.SysRolePermissionRel;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.provider.AbstractProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public interface SysRolePermissionRelMapper extends BaseMapper<SysRolePermissionRel, Long> {
	@SelectProvider(type = Provider.class, method = "queryByRoleIdAndPermId")
	SysRolePermissionRel queryByRoleIdAndPermId(@Param("roleId")long roleId, @Param("permId")long permId);

	@SelectProvider(type = Provider.class, method = "queryListByRoleId")
	List<SysRolePermissionRel> queryListByRoleId(@Param("roleId")long roleId);

	@SelectProvider(type = Provider.class, method = "queryPermIdListByRoleId")
	List<Long> queryPermIdListByRoleId(@Param("roleId")long roleId);
	
	class Provider extends AbstractProvider {
		public String queryByRoleIdAndPermId(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select ").append(getAllColumnSql(SysRolePermissionRel.class))
					.append(" from ").append(getTableName(SysRolePermissionRel.class))
					.append(" where role_id = #{roleId} and perm_id = #{permId} ");
			return builder.toString();
		}

		public String queryListByRoleId(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select ").append(getAllColumnSql(SysRolePermissionRel.class))
					.append(" from ").append(getTableName(SysRolePermissionRel.class))
					.append(" where role_id = #{roleId} ");
			return builder.toString();
		}

		public String queryPermIdListByRoleId(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select perm_id ")
					.append(" from ").append(getTableName(SysRolePermissionRel.class))
					.append(" where role_id = #{roleId} ");
			return builder.toString();
		}
	}
}
