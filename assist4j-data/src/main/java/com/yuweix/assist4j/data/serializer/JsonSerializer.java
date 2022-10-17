package com.yuweix.assist4j.data.serializer;


import com.yuweix.assist4j.core.json.Json;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * 序列化工具
 * @author yuwei
 */
public class JsonSerializer implements RedisSerializer<Object> {
	private final Json json;
	private final Charset charset;

	public JsonSerializer(Json json) {
		this(json, StandardCharsets.UTF_8);
	}
	public JsonSerializer(Json json, Charset charset) {
		Assert.notNull(charset, "[Json] must not be null!");
		this.json = json;

		Assert.notNull(charset, "[Charset] must not be null!");
		this.charset = charset;
	}

	/**
	 * 序列化
	 */
	public byte[] serialize(Object obj) {
		if (obj == null) {
			return null;
		}
		String str = json.serialize(obj);
		return str == null ? null : str.getBytes(charset);
	}

	/**
	 * 反序列化
	 */
	public Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		return json.deserialize(new String(bytes, charset));
	}
}
