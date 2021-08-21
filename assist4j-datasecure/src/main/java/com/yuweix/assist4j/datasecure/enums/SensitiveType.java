package com.yuweix.assist4j.datasecure.enums;



public enum SensitiveType {
	MOBILE_PHONE("mobile_phone", "11位手机号", "^(\\d{3})\\d{4}(\\d{4})$", "$1****$2"),//$1第一个括号的内容,$2第二个括号里面的内容
	ID_CARD("id_card", "16或者18身份证号", "^(\\d{4})\\d{8,10}(\\d{4})$", "$1****$2"),
    BANK_CARD_NO("bankCardNo", "银行卡号", "^(\\d{4})\\d*(\\d{4})$", "$1****$2"),

    CUSTOM("custom", "自定义正则处理", ""),
    TRUNCATE("truncate", "字符串截取处理", "");

    String type;
    String describe;
    String[] regular;

    SensitiveType(String type, String describe, String... regular) {
        this.type = type;
        this.describe = describe;
        this.regular = regular;
    }

    public String getType() {
        return type;
    }

    public String getDescribe() {
        return describe;
    }

    public String[] getRegular() {
        return regular;
    }
}
