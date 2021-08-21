package com.yuweix.assist4j.datasecure.serializer;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 序列化工具
 * @author yuwei
 */
public class JsonSerializer implements Serializer {
	private static final Logger log = LoggerFactory.getLogger(JsonSerializer.class);


	/**
	 * 序列化
	 */
	public <T>String serialize(T t) {
		if (t == null) {
			return null;
		}

		return JSONObject.toJSONString(t, SerializerFeature.WriteClassName);
	}

	/**
	 * 反序列化
	 */
	public <T>T deserialize(String str) {
		if (str == null) {
			return null;
		}

		if (!ParserConfig.getGlobalInstance().isAutoTypeSupport()) {
			ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		}
		return JSONObject.parseObject(str, new TypeReference<T>() {});
	}
}
