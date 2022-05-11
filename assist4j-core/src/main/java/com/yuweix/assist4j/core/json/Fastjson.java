package com.yuweix.assist4j.core.json;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 * @author yuwei
 */
public class Fastjson implements Json {
	@Override
	public <T> String toString(T t) {
		if (t == null) {
			return null;
		}

		return JSONObject.toJSONString(t, SerializerFeature.WriteClassName);
	}

	@Override
	public <T> T toObject(String str) {
		if (str == null) {
			return null;
		}

		if (!ParserConfig.getGlobalInstance().isAutoTypeSupport()) {
			ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		}
		return JSONObject.parseObject(str, new TypeReference<T>() {});
	}
}
