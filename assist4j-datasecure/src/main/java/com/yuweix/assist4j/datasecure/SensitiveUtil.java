package com.yuweix.assist4j.datasecure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


/**
 * 脱敏工具类
 */
public class SensitiveUtil {
    private static final Logger log = LoggerFactory.getLogger(SensitiveUtil.class);

    public static Object shield(Object object, String fieldName, Object val) {
        if (!(val instanceof String) || "".equals(val)) {
            return val;
        }
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            Sensitive sensitive;
            if (String.class != field.getType() || (sensitive = field.getAnnotation(Sensitive.class)) == null) {
                return val;
            }

            return shield((String) val, sensitive.regex(), sensitive.replacement());
        } catch (NoSuchFieldException e) {
            log.warn("The class {} has no field {}", object.getClass(), fieldName);
        }
        return val;
    }

    public static String shield(String val, String regex, String replacement) {
        if (val == null || "".equals(val)
                || regex == null || "".equals(regex.trim())
                || replacement == null || "".equals(replacement.trim())) {
            return val;
        }
        return val.replaceAll(regex.trim(), replacement.trim());
    }
}
