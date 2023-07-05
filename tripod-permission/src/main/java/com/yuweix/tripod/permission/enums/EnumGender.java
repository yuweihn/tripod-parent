package com.yuweix.tripod.permission.enums;




/**
 * @author yuwei
 */
public enum EnumGender {
	MALE((byte) 1, "男"),
	FEMALE((byte) 2, "女");
	
	
	private byte code;
	private String name;
	
	EnumGender(byte code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public byte getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static EnumGender getByCode(byte code) {
		for (EnumGender p: EnumGender.values()) {
			if (p.code == code) {
				return p;
			}
		}
		return null;
	}
	
	public static String getNameByCode(byte code) {
		for (EnumGender p: EnumGender.values()) {
			if (p.code == code) {
				return p.name;
			}
		}
		return "";
	}
}
