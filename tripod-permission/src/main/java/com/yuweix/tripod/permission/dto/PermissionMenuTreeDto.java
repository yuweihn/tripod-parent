package com.yuweix.tripod.permission.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class PermissionMenuTreeDto extends AbstractTreeDto<PermissionMenuTreeDto> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String path;
	private String name;
	private String permType;
	private String component;
	private Boolean ifExt;
	private Boolean hidden;
	private Meta meta;
	private Long parentId;

	private List<PermissionMenuTreeDto> children = new ArrayList<>();


	public PermissionMenuTreeDto addChild(PermissionMenuTreeDto child) {
		children.add(child);
		return this;
	}

	public static class Meta implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private String title;
		private String icon;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermType() {
		return permType;
	}

	public void setPermType(String permType) {
		this.permType = permType;
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

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	@Override
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<PermissionMenuTreeDto> getChildren() {
		return children;
	}

	public void setChildren(List<PermissionMenuTreeDto> children) {
		this.children = children;
	}
}
