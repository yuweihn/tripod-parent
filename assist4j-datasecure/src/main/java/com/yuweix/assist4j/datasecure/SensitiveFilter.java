package com.yuweix.assist4j.datasecure;


import com.alibaba.fastjson.serializer.ValueFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


public class SensitiveFilter implements ValueFilter {
    private static final Logger log = LoggerFactory.getLogger(SensitiveFilter.class);

	@Override
    public Object process(Object object, String name, Object value) {
        if (!(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
        try {
            Field field = object.getClass().getDeclaredField(name);
            Sensitive sensitive;
            if (String.class != field.getType() || (sensitive = field.getAnnotation(Sensitive.class)) == null) {
                return value;
            }
            
            return SensitiveUtil.shield((String) value, sensitive);
        } catch (NoSuchFieldException e) {
            log.warn("The class {} has no field {}", object.getClass(), name);
        }
        return value;
    }
}
