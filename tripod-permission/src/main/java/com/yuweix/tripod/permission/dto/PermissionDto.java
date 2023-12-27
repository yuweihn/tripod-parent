package com.yuweix.tripod.permission.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class PermissionDto extends AbstractTreeDto<PermissionDto> implements Serializable {
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

	private List<PermissionDto> children = new ArrayList<>();

	private String creator;
	private String createTime;
	private String modifier;
	private String modifyTime;


	public PermissionDto addChild(PermissionDto child) {
		children.add(child);
		return this;
	}

	//////////////////////////////////////////////////////////////////////////////
	@Override
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

	@Override
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

	public List<PermissionDto> getChildren() {
		return children;
	}

	public void setChildren(List<PermissionDto> children) {
		this.children = children;
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
