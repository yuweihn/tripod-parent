package com.yuweix.tripod.data.serializer;


import com.yuweix.tripod.core.json.Json;


/**
 * 序列化工具
 * @author yuwei
 */
public class JsonSerializer implements Serializer {
//	private static final Logger log = LoggerFactory.getLogger(JsonSerializer.class);

	private Json json;

	public JsonSerializer(Json json) {
		this.json = json;
	}

	/**
	 * 序列化
	 */
	public <T>String serialize(T t) {
		return t == null ? null : json.serialize(t);
	}

	/**
	 * 反序列化
	 */
	public <T>T deserialize(String str) {
		return str == null ? null : json.deserialize(str);
	}
}
