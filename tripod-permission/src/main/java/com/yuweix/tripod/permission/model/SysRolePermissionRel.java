package com.wei.ai.model;


import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author yuwei
 */
@Table(name = "sys_role_permission_rel")
@Data
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
}
