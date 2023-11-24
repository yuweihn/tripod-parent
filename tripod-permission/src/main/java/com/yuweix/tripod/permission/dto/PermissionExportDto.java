package com.yuweix.tripod.permission.dto;


import com.yuweix.tripod.core.encrypt.SecurityUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class PermissionExportDto implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private List<Body> list = new ArrayList<>();
	private String sign;

	private static final String SALT = "Rewfe5..;;43po6baasfpcxr034;;',";


	public PermissionExportDto addBody(Body body) {
		list.add(body);
		return this;
	}

	public PermissionExportDto sign() {
		this.sign = SecurityUtil.getMd5(list + SALT);
		return this;
	}

	public boolean verify() {
		String sign0 = SecurityUtil.getMd5(list + SALT);
		return sign0.equals(this.sign);
	}

	public static class Body implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private long id;
		private String permNo;
		private String title;
		private Long parentId;
		private Integer orderNum;
		private String path;
		private String component;
		private Boolean ifExt;
		private String permType;
		private String permTypeName;
		private Boolean visible;
		private String icon;
		private String descr;
		private String creator;
		private String createTime;
		private String modifier;
		private String modifyTime;


		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getPermNo() {
			return permNo;
		}

		public void setPermNo(String permNo) {
			this.permNo = permNo;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Long getParentId() {
			return parentId;
		}

		public void setParentId(Long parentId) {
			this.parentId = parentId;
		}

		public Integer getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(Integer orderNum) {
			this.orderNum = orderNum;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getComponent() {
			return component;
		}

		public void setComponent(String component) {
			this.component = component;
		}

		public Boolean getIfExt() {
			return ifExt;
		}

		public void setIfExt(Boolean ifExt) {
			this.ifExt = ifExt;
		}

		public String getPermType() {
			return permType;
		}

		public void setPermType(String permType) {
			this.permType = permType;
		}

		public String getPermTypeName() {
			return permTypeName;
		}

		public void setPermTypeName(String permTypeName) {
			this.permTypeName = permTypeName;
		}

		public Boolean getVisible() {
			return visible;
		}

		public void setVisible(Boolean visible) {
			this.visible = visible;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getDescr() {
			return descr;
		}

		public void setDescr(String descr) {
			this.descr = descr;
		}

		public String getCreator() {
			return creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getModifier() {
			return modifier;
		}

		public void setModifier(String modifier) {
			this.modifier = modifier;
		}

		public String getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}
	}

	public List<Body> getList() {
		return list;
	}

	public void setList(List<Body> list) {
		this.list = list;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
