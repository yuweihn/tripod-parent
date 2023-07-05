package com.wei.ai.model;


import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author yuwei
 */
@Table(name = "sys_permission")
@Data
public class SysPermission implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "hehe")
	private long id;

	@Column(name = "perm_no")
	private String permNo;

	@Column(name = "title")
	private String title;

	@Column(name = "parent_id")
	private Long parentId;

	@Column(name = "order_num")
	private int orderNum;

	@Column(name = "path")
	private String path;

	@Column(name = "component")
	private String component;

	@Column(name = "if_ext")
	private boolean ifExt;

	/**
	 * 见 {@link com.wei.ai.common.enums.PermType}
	 */
	@Column(name = "perm_type")
	private String permType;

	@Column(name = "visible")
	private boolean visible;

	@Column(name = "icon")
	private String icon;

	@Column(name = "descr")
	private String descr;


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
