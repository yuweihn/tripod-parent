package com.yuweix.tripod.permission.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author yuwei
 */
@Table(name = "sys_role_permission_rel")
public class SysRolePermissionRel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "hehe")
	private long id;
	
	@Column(name = "role_id")
	private long roleId;
	
	@Column(name = "perm_id")
	private long permId;
	
	
	@Version
	@Column(name = "version")
	private int version;
	
	@Column(name = "creator")
	private String creator;
	
	@Column(name = "create_time")
	private Date createTime;
	
	@Column(name = "modifier")
	private String modifier;
	
	@Column(name = "modify_time")
	private Date modifyTime;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getPermId() {
		return permId;
	}

	public void setPermId(long permId) {
		this.permId = permId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}
