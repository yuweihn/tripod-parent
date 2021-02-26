package com.yuweix.assist4j.data.serializer;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 序列化工具
 * @author yuwei
 */
public class DefaultSerializer implements Serializer {
	private static final Logger log = LoggerFactory.getLogger(DefaultSerializer.class);


	/**
	 * 序列化
	 */
	public <T>String serialize(T t) {
		if (t == null) {
			return null;
		}

		return JSONObject.toJSONString(new JsonText(t.getClass().getName(), t));
	}

	/**
	 * 反序列化
	 */
	public <T>T deserialize(String str) {
		if (str == null) {
			return null;
		}

		JsonText jsonText = JSONObject.parseObject(str, JsonText.class);
		T t = null;
		try {
			t = (T) JSONObject.parseObject(JSONObject.toJSONString(jsonText.getText()), Class.forName(jsonText.getClz()));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return t;
	}
}
