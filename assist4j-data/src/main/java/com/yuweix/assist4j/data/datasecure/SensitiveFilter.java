package com.yuweix.assist4j.data.datasecure;


import com.alibaba.fastjson.serializer.ValueFilter;


public class SensitiveFilter implements ValueFilter {
	@Override
    public Object process(Object object, String name, Object value) {
        return SensitiveUtil.shield(object, name, value);
    }
}
