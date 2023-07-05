package com.wei.ai.model;


import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author yuwei
 */
@Table(name = "sys_admin")
@Data
public class SysAdmin implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "hehe")
	private long id;
	
	@Column(name = "account_no")
	private String accountNo;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "real_name")
	private String realName;
	
	/**
	 * 见 EnumGender.java
	 */
	@Column(name = "gender")
	private Byte gender;
	
	@Column(name = "avatar")
	private String avatar;

	@Column(name = "last_login_time")
	private Date lastLoginTime;

	@Column(name = "last_login_ip")
	private String lastLoginIp;

	
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
