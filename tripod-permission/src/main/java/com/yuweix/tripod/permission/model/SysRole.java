package com.wei.ai.model;


import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author yuwei
 */
@Table(name = "sys_role")
@Data
public class SysRole implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "hehe")
	private long id;

	@Column(name = "role_no")
	private String roleNo;
	
	@Column(name = "role_name")
	private String roleName;
	
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
