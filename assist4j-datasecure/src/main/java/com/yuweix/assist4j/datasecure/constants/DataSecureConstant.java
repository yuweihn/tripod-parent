package com.yuweix.assist4j.datasecure.constants;




public class DataSecureConstant {
    /** 双引号(") */
    public static final String DOUBLE_QUOTATION = "\"";

    /** 组JSON用(":") */
    public static final String MARK_JSON = "\":\"";

    /** 等于号 */
    public static final String MARK_EQUAL = "=";

    /** JSON格式key:value正则 */
    public static final String REGEX_JSON = "\\\\*\"({0})\\\\*\":\\\\*\"([^\"\\\\]+)\\\\*\"";

    // 等于符表示的正则表达式
    public static final String REGEX_EQUAL = "([^,\\s\\d-\\[\\]&?{}][\\s*\\w]+?)=([^,\"\\*\\[\\]{}<]\\s*\\w+)";
}
