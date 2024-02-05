package com.yuweix.tripod.permission.enums;




/**
 * @author yuwei
 */
public enum PermType {
	DIR("D", "目录"),
	MENU("M", "菜单"),
	BUTTON("B", "按钮");


	private String code;
	private String name;

	PermType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static PermType getByCode(String code) {
		for (PermType p: PermType.values()) {
			if (p.code.equals(code)) {
				return p;
			}
		}
		return null;
	}
	
	public static String getNameByCode(String code) {
		for (PermType p: PermType.values()) {
			if (p.code.equals(code)) {
				return p.name;
			}
		}
		return "";
	}
}
