package com.yuweix.assist4j.datasecure.utils;


import com.yuweix.assist4j.datasecure.annotations.SensitiveInfo;
import com.yuweix.assist4j.datasecure.constants.DataSecureConstant;
import com.yuweix.assist4j.datasecure.enums.SensitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
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
    public static String shield(SensitiveInfo desensitization, String info) {
        //默认不显示/不记录
        if (desensitization == null || info == null || "".equals(info)) {
            return info;
        }
        List<String> regular;
        SensitiveType type = desensitization.value();
        switch (type) {
            case CUSTOM:
                regular = Arrays.asList(desensitization.attach());
                break;
            case TRUNCATE:
                regular = truncateRender(desensitization.attach());
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

    private static List<String>  truncateRender(String[] attachs) {
        List<String> regular = new ArrayList<>();
        if (null != attachs && attachs.length > 1) {
            String rule = attachs[0];
            String size = attachs[1];
            String template, result;
            if ("0".equals(rule)) {
                template = "^(\\S{%s})(\\S+)$";
                result = "$1";
            } else if ("1".equals(rule)) {
                template = "^(\\S+)(\\S{%s})$";
                result = "$2";
            } else {
                return regular;
            }
            try {
                if (Integer.parseInt(size) > 0) {
                    regular.add(0, String.format(template, size));
                    regular.add(1, result);
                }
            } catch (Exception e) {
            	log.warn("ValueDesensitizeFilter truncateRender size {} exception", size);
            }
        }
        return regular;
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
                    builder.append(DataSecureConstant.DOUBLE_QUOTATION).append(matcher.group(1))
                            .append(DataSecureConstant.MARK_JSON)
                            .append(shield(fields.get(fieldName), matcher.group(2)))
                            .append(DataSecureConstant.DOUBLE_QUOTATION);
//                    jsonVal = StringUtils.replace(jsonVal, matcher.group(0), builder.toString());
                    jsonVal = jsonVal.replace(matcher.group(0), builder.toString());
                }
            }
            return jsonVal;
        } catch (Exception e) {
            log.warn("[jsonShield]JSON字符串脱敏异常,注意JSON格式", e);
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
                        builder.append(matcher.group(1)).append(DataSecureConstant.MARK_EQUAL)
                                .append(SensitiveProcessUtil.shield(fields.get(fieldName), matcher.group(2)));
//                        srcData = StringUtils.replace(srcData, matcher.group(0), builder.toString());
                        srcData = srcData.replace(matcher.group(0), builder.toString());
                    }
                }
                String fieldRegex = MessageFormat.format(DataSecureConstant.REGEX_JSON, fieldName);
                matcher = Pattern.compile(fieldRegex).matcher(srcData);
                while (matcher.find()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(DataSecureConstant.DOUBLE_QUOTATION).append(matcher.group(1))
                            .append(DataSecureConstant.MARK_JSON)
                            .append(SensitiveProcessUtil.shield(fields.get(fieldName), matcher.group(2)))
                            .append(DataSecureConstant.DOUBLE_QUOTATION);
//                    srcData = StringUtils.replace(srcData, matcher.group(0), builder.toString());
                    srcData = srcData.replace(matcher.group(0), builder.toString());
                }
            }
            return srcData;
        } catch (Exception e) {
            return srcData;
        }
    }
}
