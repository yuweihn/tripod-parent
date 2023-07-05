package com.wei.ai.mapper;


import com.wei.ai.model.SysPermission;
import com.yuweix.tripod.dao.mybatis.BaseMapper;
import com.yuweix.tripod.dao.mybatis.provider.AbstractProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission, Long> {
	@SelectProvider(type = Provider.class, method = "queryPermissionCount")
	int queryPermissionCount(@Param("idList")List<Long> idList, @Param("parentId")Long parentId
			, @Param("keywords")String keywords, @Param("permTypeList")List<String> permTypeList
			, @Param("visible")Boolean visible);

	@SelectProvider(type = Provider.class, method = "queryPermissionList")
	List<SysPermission> queryPermissionList(@Param("idList")List<Long> idList, @Param("parentId")Long parentId
			, @Param("keywords")String keywords, @Param("permTypeList")List<String> permTypeList
			, @Param("visible")Boolean visible
			, @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);

	@SelectProvider(type = Provider.class, method = "queryPermissionByNo")
	SysPermission queryPermissionByNo(@Param("permNo")String permNo);

	class Provider extends AbstractProvider {
		@SuppressWarnings("unchecked")
		public String queryPermissionCount(Map<String, Object> param) {
			List<Long> idList = (List<Long>) param.get("idList");
			Long parentId = (Long) param.get("parentId");
			String keywords = (String) param.get("keywords");
			List<String> permTypeList = (List<String>) param.get("permTypeList");
			Boolean visible = (Boolean) param.get("visible");

			StringBuilder builder = new StringBuilder("");
			builder.append("<script>");
			builder.append(" select count(id) ");
			builder.append(" from ").append(getTableName(SysPermission.class));
			builder.append(" where 1 = 1 ");

			if (idList != null) {
				if (idList.size() <= 0) {
					builder.append(" and 1 = 0 ");
				} else {
					builder.append(" and id in <foreach item='item' collection='idList' open='(' separator=',' close=')'>#{item}</foreach> ");
				}
			}
			if (parentId != null) {
				builder.append(" and parent_id = #{parentId} ");
			}
			if (keywords != null && !"".equals(keywords.trim())) {
				param.put("keywords", "%" + keywords.trim() + "%");
				builder.append(" and (perm_no like #{keywords} or title like #{keywords}) ");
			}
			if (permTypeList != null) {
				if (permTypeList.size() <= 0) {
					builder.append(" and 1 = 0 ");
				} else {
					builder.append(" and perm_type in <foreach item='item' collection='permTypeList' open='(' separator=',' close=')'>#{item}</foreach> ");
				}
			}
			if (visible != null) {
				builder.append(" and visible = #{visible} ");
			}
			builder.append("</script>");
			return builder.toString();
		}

		@SuppressWarnings("unchecked")
		public String queryPermissionList(Map<String, Object> param) {
			List<Long> idList = (List<Long>) param.get("idList");
			Long parentId = (Long) param.get("parentId");
			String keywords = (String) param.get("keywords");
			List<String> permTypeList = (List<String>) param.get("permTypeList");
			Boolean visible = (Boolean) param.get("visible");
			Integer pageNo = (Integer) param.get("pageNo");
			Integer pageSize = (Integer) param.get("pageSize");

			StringBuilder builder = new StringBuilder("");
			builder.append("<script>");
			builder.append(" select ").append(getAllColumnSql(SysPermission.class));
			builder.append(" from ").append(getTableName(SysPermission.class));
			builder.append(" where 1 = 1 ");

			if (idList != null) {
				if (idList.size() <= 0) {
					builder.append(" and 1 = 0 ");
				} else {
					builder.append(" and id in <foreach item='item' collection='idList' open='(' separator=',' close=')'>#{item}</foreach> ");
				}
			}
			if (parentId != null) {
				builder.append(" and parent_id = #{parentId} ");
			}
			if (keywords != null && !"".equals(keywords.trim())) {
				param.put("keywords", "%" + keywords.trim() + "%");
				builder.append(" and (perm_no like #{keywords} or title like #{keywords}) ");
			}
			if (permTypeList != null) {
				if (permTypeList.size() <= 0) {
					builder.append(" and 1 = 0 ");
				} else {
					builder.append(" and perm_type in <foreach item='item' collection='permTypeList' open='(' separator=',' close=')'>#{item}</foreach> ");
				}
			}
			if (visible != null) {
				builder.append(" and visible = #{visible} ");
			}
			builder.append(" order by order_num, id ");
			if (pageNo != null && pageSize != null) {
				builder.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
			}
			builder.append("</script>");
			return builder.toString();
		}

		public String queryPermissionByNo(Map<String, Object> param) {
			String permNo = (String) param.get("permNo");

			StringBuilder builder = new StringBuilder("");
			builder.append("  select ").append(getAllColumnSql(SysPermission.class))
					.append(" from ").append(getTableName(SysPermission.class))
					.append(" where 1 = 1 ");
			if (permNo != null && !"".equals(permNo.trim())) {
				param.put("permNo", permNo.trim());
				builder.append(" and perm_no = #{permNo} ");
			} else {
				builder.append(" and 1 = 0 ");
			}
			return builder.toString();
		}
	}
}
