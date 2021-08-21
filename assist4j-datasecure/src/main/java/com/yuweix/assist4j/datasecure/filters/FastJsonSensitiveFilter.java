package com.yuweix.assist4j.datasecure.filters;


import com.alibaba.fastjson.serializer.ValueFilter;
import com.yuweix.assist4j.datasecure.annotations.SensitiveInfo;
import com.yuweix.assist4j.datasecure.utils.SensitiveProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


public class FastJsonSensitiveFilter implements ValueFilter {
    private static final Logger log = LoggerFactory.getLogger(FastJsonSensitiveFilter.class);

	@Override
    public Object process(Object object, String name, Object value) {
        if (!(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
        try {
            Field field = object.getClass().getDeclaredField(name);
            SensitiveInfo desensitization;
            if (String.class != field.getType() || (desensitization = field.getAnnotation(SensitiveInfo.class)) == null) {
                return value;
            }
            
            return SensitiveProcessUtil.shield(desensitization, (String)value);
        } catch (NoSuchFieldException e) {
            log.warn("The class {} has no field {}", object.getClass(), name);
        }
        return value;
    }
}
