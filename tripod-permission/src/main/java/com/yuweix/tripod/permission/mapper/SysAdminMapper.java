package com.wei.ai.mapper;


import com.wei.ai.model.SysAdmin;
import com.wei.ai.model.SysAdminRoleRel;
import com.wei.ai.model.SysRolePermissionRel;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.provider.AbstractProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public interface SysAdminMapper extends BaseMapper<SysAdmin, Long> {
	@SelectProvider(type = Provider.class, method = "findAdminByAccountNo")
	SysAdmin findAdminByAccountNo(@Param("accountNo")String accountNo);
	
	@SelectProvider(type = Provider.class, method = "hasPermission")
	boolean hasPermission(@Param("adminId")long adminId, @Param("permissionId")long permissionId);

	@SelectProvider(type = Provider.class, method = "queryPermissionIdListByAdminId")
	List<Long> queryPermissionIdListByAdminId(@Param("adminId")long adminId);

	@SelectProvider(type = Provider.class, method = "findCountByRoleId")
	int findCountByRoleId(@Param("roleId")long roleId, @Param("keywords")String keywords);

	@SelectProvider(type = Provider.class, method = "findListByRoleId")
	List<SysAdmin> findListByRoleId(@Param("roleId")long roleId, @Param("keywords")String keywords
			, @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);
	
	
	class Provider extends AbstractProvider {
		public String findAdminByAccountNo(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select ").append(getAllColumnSql(SysAdmin.class))
					.append(" from ").append(getTableName(SysAdmin.class))
					.append(" where account_no = #{accountNo} ");
			return builder.toString();
		}
		
		public String hasPermission(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select count(a.id) > 0 ")
					.append(" from ").append(getTableName(SysAdminRoleRel.class)).append(" a ")
					.append(" inner join ").append(getTableName(SysRolePermissionRel.class)).append(" b on a.admin_id = #{adminId} and a.role_id = b.role_id and b.perm_id = #{permissionId} ");
			return builder.toString();
		}

		public String queryPermissionIdListByAdminId(Map<String, Object> param) {
			StringBuilder builder = new StringBuilder("");
			builder.append("  select distinct b.perm_id ")
					.append(" from ").append(getTableName(SysAdminRoleRel.class)).append(" a ")
					.append(" inner join ").append(getTableName(SysRolePermissionRel.class)).append(" b on a.admin_id = #{adminId} and a.role_id = b.role_id ");
			return builder.toString();
		}

		public String findCountByRoleId(Map<String, Object> param) {
			String keywords = (String) param.get("keywords");

			StringBuilder builder = new StringBuilder("");
			builder.append(" select count(distinct b.id) as cnt ");
			builder.append(" from ").append(getTableName(SysAdminRoleRel.class)).append(" a ");
			builder.append(" inner join ").append(getTableName(SysAdmin.class)).append(" b on a.admin_id = b.id ");
			builder.append(" where a.role_id = #{roleId} ");
			if (keywords != null && !"".equals(keywords.trim())) {
				param.put("keywords", "%" + keywords.trim() + "%");
				builder.append(" and (b.account_no like #{keywords} or b.real_name like #{keywords}) ");
			}
			return builder.toString();
		}

		public String findListByRoleId(Map<String, Object> param) {
			String keywords = (String) param.get("keywords");
			Integer pageNo = (Integer) param.get("pageNo");
			Integer pageSize = (Integer) param.get("pageSize");

			StringBuilder builder = new StringBuilder("");
			builder.append(" select distinct ").append(getAllColumnSql(SysAdmin.class, "b"));
			builder.append(" from ").append(getTableName(SysAdminRoleRel.class)).append(" a ");
			builder.append(" inner join ").append(getTableName(SysAdmin.class)).append(" b on a.admin_id = b.id ");
			builder.append(" where a.role_id = #{roleId} ");
			if (keywords != null && !"".equals(keywords.trim())) {
				param.put("keywords", "%" + keywords.trim() + "%");
				builder.append(" and (b.account_no like #{keywords} or b.real_name like #{keywords}) ");
			}
			if (pageNo != null && pageSize != null) {
				builder.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
			}
			return builder.toString();
		}
	}
}
