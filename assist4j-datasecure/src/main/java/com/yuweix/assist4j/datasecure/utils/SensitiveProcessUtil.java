package com.yuweix.assist4j.datasecure.utils;


import com.yuweix.assist4j.datasecure.annotations.Sensitive;
import com.yuweix.assist4j.datasecure.constants.DataSecureConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 脱敏工具类
 */
public class SensitiveProcessUtil {
    private static final Logger log = LoggerFactory.getLogger(SensitiveProcessUtil.class);


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

    /**
     * JSON字符串脱敏
     * @param jsonVal 待脱敏字符串
     * @param fields 脱敏字段及规则
     * @return
     */
    public static String shieldJson(String jsonVal, Map<String, SensitiveType> fields) {
        if (fields == null || fields.isEmpty() || jsonVal == null || "".equals(jsonVal)) {
            return jsonVal;
        }
        try {
            for (String fieldName: fields.keySet()) {
                String fieldRegex = MessageFormat.format(DataSecureConstant.REGEX_JSON, fieldName);
                Matcher matcher = Pattern.compile(fieldRegex).matcher(jsonVal);
                while (matcher.find()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(DataSecureConstant.DOUBLE_QUOTATION)
                            .append(matcher.group(1))
                            .append(DataSecureConstant.MARK_JSON)
                            .append(shield(matcher.group(2), fields.get(fieldName)))
                            .append(DataSecureConstant.DOUBLE_QUOTATION);
                    jsonVal = jsonVal.replace(matcher.group(0), builder.toString());
                }
            }
            return jsonVal;
        } catch (Exception e) {
            log.warn("JSON字符串脱敏格式异常。", e);
            return jsonVal;
        }
    }

    /**
     * 纯数据脱敏
     * @param src
     * @param fields
     * @return
     */
    public static String shield(String src, Map<String, SensitiveType> fields) {
        if (fields == null || fields.isEmpty() || src == null || "".equals(src)) {
            return src;
        }
        try {
            for (String fieldName: fields.keySet()) {
                Matcher matcher = Pattern.compile(DataSecureConstant.REGEX_EQUAL).matcher(src);
                while (matcher.find()) {
                    if (matcher.group(1).trim().equals(fieldName)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(matcher.group(1))
                                .append(DataSecureConstant.MARK_EQUAL)
                                .append(SensitiveProcessUtil.shield(matcher.group(2), fields.get(fieldName)));
                        src = src.replace(matcher.group(0), builder.toString());
                    }
                }
                String fieldRegex = MessageFormat.format(DataSecureConstant.REGEX_JSON, fieldName);
                matcher = Pattern.compile(fieldRegex).matcher(src);
                while (matcher.find()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(DataSecureConstant.DOUBLE_QUOTATION)
                            .append(matcher.group(1))
                            .append(DataSecureConstant.MARK_JSON)
                            .append(SensitiveProcessUtil.shield(matcher.group(2), fields.get(fieldName)))
                            .append(DataSecureConstant.DOUBLE_QUOTATION);
                    src = src.replace(matcher.group(0), builder.toString());
                }
            }
            return src;
        } catch (Exception e) {
            log.warn("字符串脱敏异常。", e);
            return src;
        }
    }
}
