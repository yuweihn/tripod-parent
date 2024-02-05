package com.yuweix.tripod.permission.dto;




/**
 * @author yuwei
 */
public abstract class AbstractTreeDto<T extends AbstractTreeDto<T>> {
	public abstract long getId();

	public abstract Long getParentId();

	public abstract T addChild(T child);
}
