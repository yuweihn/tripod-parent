package com.yuweix.tripod.permission.mapper;


import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.provider.AbstractProvider;
import com.yuweix.tripod.permission.dto.SysAdminRoleDto;
import com.yuweix.tripod.permission.model.SysAdmin;
import com.yuweix.tripod.permission.model.SysAdminRoleRel;
import com.yuweix.tripod.permission.model.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public interface SysAdminRoleRelMapper extends BaseMapper<SysAdminRoleRel, Long> {
	@SelectProvider(type = Provider.class, method = "hasRole")
	boolean hasRole(@Param("adminId")long adminId, @Param("roleId")long roleId);

	@SelectProvider(type = Provider.class, method = "queryByAdminIdAndRoleId")
	SysAdminRoleRel queryByAdminIdAndRoleId(@Param("adminId")long adminId, @Param("roleId")long roleId);

	@SelectProvider(type = Provider.class, method = "queryAdminRoleCountByAdminId")
	int queryAdminRoleCountByAdminId(@Param("adminId")Long adminId, @Param("roleId")Long roleId, @Param("keywords")String keywords);

	@SelectProvider(type = Provider.class, method = "queryAdminRoleListByAdminId")
	List<SysAdminRoleDto> queryAdminRoleListByAdminId(@Param("adminId")Long adminId, @Param("roleId")Long roleId
			, @Param("keywords")String keywords, @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);

	class Provider extends AbstractProvider {
		public String hasRole(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select count(id) > 0 ")
					.append(" from ").append(getTableName(SysAdminRoleRel.class))
					.append(" where admin_id = #{adminId} and role_id = #{roleId} ");
			return builder.toString();
		}

		public String queryByAdminIdAndRoleId(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select ").append(getAllColumnSql(SysAdminRoleRel.class))
					.append(" from ").append(getTableName(SysAdminRoleRel.class))
					.append(" where admin_id = #{adminId} and role_id = #{roleId} ");
			return builder.toString();
		}

		public String queryAdminRoleCountByAdminId(Map<String, Object> param) {
			Long adminId = (Long) param.get("adminId");
			Long roleId = (Long) param.get("roleId");
			String keywords = (String) param.get("keywords");

			StringBuilder builder = new StringBuilder("");
			builder.append("  select count(a.id) as cnt ")
					.append(" from ").append(getTableName(SysAdminRoleRel.class)).append(" a ")
					.append(" left join ").append(getTableName(SysRole.class)).append(" b on a.role_id = b.id ")
					.append(" left join ").append(getTableName(SysAdmin.class)).append(" c on a.admin_id = c.id ")
					.append(" where 1 = 1 ");
			if (adminId != null) {
				builder.append(" and a.admin_id = #{adminId} ");
			}
			if (roleId != null) {
				builder.append(" and a.role_id = #{roleId} ");
			}
			if (keywords != null && !"".equals(keywords.trim())) {
				param.put("keywords", "%" + keywords.trim() + "%");
				builder.append(" and (b.role_no like #{keywords} or b.role_name like #{keywords} or c.account_no like #{keywords} or c.real_name like #{keywords}) ");
			}
			return builder.toString();
		}

		public String queryAdminRoleListByAdminId(Map<String, Object> param) {
			Long adminId = (Long) param.get("adminId");
			Long roleId = (Long) param.get("roleId");
			String keywords = (String) param.get("keywords");
			Integer pageNo = (Integer) param.get("pageNo");
			Integer pageSize = (Integer) param.get("pageSize");

			StringBuilder builder = new StringBuilder("");
			builder.append("  select a.id, a.admin_id as adminId, a.role_id as roleId, b.role_no as roleNo, b.role_name as roleName ")
					.append("           , a.creator, a.create_time as createTime, a.modifier, a.modify_time as modifyTime ")
					.append(" from ").append(getTableName(SysAdminRoleRel.class)).append(" a ")
					.append(" left join ").append(getTableName(SysRole.class)).append(" b on a.role_id = b.id ")
					.append(" left join ").append(getTableName(SysAdmin.class)).append(" c on a.admin_id = c.id ")
					.append(" where 1 = 1 ");
			if (adminId != null) {
				builder.append(" and a.admin_id = #{adminId} ");
			}
			if (roleId != null) {
				builder.append(" and a.role_id = #{roleId} ");
			}
			if (keywords != null && !"".equals(keywords.trim())) {
				param.put("keywords", "%" + keywords.trim() + "%");
				builder.append(" and (b.role_no like #{keywords} or b.role_name like #{keywords} or c.account_no like #{keywords} or c.real_name like #{keywords}) ");
			}
			builder.append(" order by a.id ");
			if (pageNo != null && pageSize != null) {
				builder.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
			}
			return builder.toString();
		}
	}
}
