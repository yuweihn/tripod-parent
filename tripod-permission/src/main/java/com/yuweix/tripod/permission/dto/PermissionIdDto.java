package com.yuweix.tripod.permission.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class PermissionIdDto extends AbstractTreeDto<PermissionIdDto> implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	private Long parentId;
	private List<PermissionIdDto> children = new ArrayList<>();


	public PermissionIdDto addChild(PermissionIdDto child) {
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

	@Override
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<PermissionIdDto> getChildren() {
		return children;
	}

	public void setChildren(List<PermissionIdDto> children) {
		this.children = children;
	}
}
