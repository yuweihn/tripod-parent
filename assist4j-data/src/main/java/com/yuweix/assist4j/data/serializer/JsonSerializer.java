package com.yuweix.assist4j.data.serializer;


import com.yuweix.assist4j.core.json.Json;


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
		if (t == null) {
			return null;
		}
		return json.toString(t);
	}

	/**
	 * 反序列化
	 */
	public <T>T deserialize(String str) {
		if (str == null) {
			return null;
		}
		return json.toObject(str);
	}
}
