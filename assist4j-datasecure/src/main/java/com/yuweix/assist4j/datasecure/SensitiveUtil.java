package com.yuweix.assist4j.datasecure;




/**
 * 脱敏工具类
 */
public class SensitiveUtil {
    /**
     * 按敏感信息格式化转换字符串信息
     * @return
     */
    public static String shield(String info, Sensitive sensitive) {
        if (sensitive == null || info == null || "".equals(info)) {
            return info;
        }

        String regex = sensitive.regex();
        String replacement = sensitive.replacement();
        if (regex == null || "".equals(regex.trim())
                || replacement == null || "".equals(replacement.trim())) {
            return info;
        }
        return info.replaceAll(regex.trim(), replacement.trim());
    }
}
