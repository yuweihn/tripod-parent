package com.yuweix.assist4j.datasecure.utils;


import com.yuweix.assist4j.datasecure.annotations.Sensitive;
import com.yuweix.assist4j.datasecure.constants.DataSecureConstant;
import com.yuweix.assist4j.datasecure.enums.SensitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public static String shield(Sensitive sensitive, String info) {
        //默认不显示/不记录
        if (sensitive == null || info == null || "".equals(info)) {
            return info;
        }
        List<String> regular;
        SensitiveType type = sensitive.value();
        switch (type) {
            case CUSTOM:
                regular = Arrays.asList(sensitive.attach());
                break;
            default:
                regular = Arrays.asList(type.getRegular());
        }
        if (regular.size() > 1) {
            String match = regular.get(0);
            String result = regular.get(1);
            if (null != match && result != null && match.length() > 0) {
                return info.replaceAll(match, result);
            }
        }
        return info;
    }
    
    public static String shield(SensitiveType type, String info) {
        //默认不显示/不记录
        if (type == null || info == null || "".equals(info)) {
            return info;
        }
        List<String> regular = Arrays.asList(type.getRegular());
        if (regular.size() > 1) {
            String match = regular.get(0);
            String result = regular.get(1);
            if (null != match && result != null && match.length() > 0) {
                return info.replaceAll(match, result);
            }
        }
        return info;
    }

    /**
     * JSON字符串脱敏
     * 
     * @param jsonVal 待脱敏字符串
     * @param fields 脱敏字段及规则
     * @return
     */
    public static String jsonShield(String jsonVal, Map<String, SensitiveType> fields) {
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
                            .append(shield(fields.get(fieldName), matcher.group(2)))
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
     * @param srcData
     * @param fields
     * @return
     */
    public static String dataShield(String srcData, Map<String, SensitiveType> fields) {
        if (fields == null || fields.isEmpty() || srcData == null || "".equals(srcData)) {
            return srcData;
        }
        try {
            for (String fieldName: fields.keySet()) {
                Matcher matcher = Pattern.compile(DataSecureConstant.REGEX_EQUAL).matcher(srcData);
                while (matcher.find()) {
                    if (matcher.group(1).trim().equals(fieldName)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(matcher.group(1))
                                .append(DataSecureConstant.MARK_EQUAL)
                                .append(SensitiveProcessUtil.shield(fields.get(fieldName), matcher.group(2)));
                        srcData = srcData.replace(matcher.group(0), builder.toString());
                    }
                }
                String fieldRegex = MessageFormat.format(DataSecureConstant.REGEX_JSON, fieldName);
                matcher = Pattern.compile(fieldRegex).matcher(srcData);
                while (matcher.find()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(DataSecureConstant.DOUBLE_QUOTATION)
                            .append(matcher.group(1))
                            .append(DataSecureConstant.MARK_JSON)
                            .append(SensitiveProcessUtil.shield(fields.get(fieldName), matcher.group(2)))
                            .append(DataSecureConstant.DOUBLE_QUOTATION);
                    srcData = srcData.replace(matcher.group(0), builder.toString());
                }
            }
            return srcData;
        } catch (Exception e) {
            return srcData;
        }
    }
}
