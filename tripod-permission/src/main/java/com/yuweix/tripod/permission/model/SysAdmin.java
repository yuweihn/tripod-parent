package com.yuweix.tripod.permission.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author yuwei
 */
@Table(name = "sys_admin")
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


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Byte getGender() {
		return gender;
	}

	public void setGender(Byte gender) {
		this.gender = gender;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
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
