package com.assist4j.data.cache.serialize;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.assist4j.data.cache.CacheClzEnum;
import com.assist4j.data.cache.CacheValue;
import com.assist4j.data.cache.ValueData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;


/**
 * @author yuwei
 */
public class DefaultSerialize implements Serialize {
	private static final Logger log = LoggerFactory.getLogger(DefaultSerialize.class);


	@Override
	public <T> String encode(T t) {
//		validCacheValue(value);

		Class<?> vClz = t.getClass();
		ValueData vd = new ValueData();
		vd.setClassName(vClz.getName());

		if (CacheValue.class.isAssignableFrom(vClz)) {
			CacheValue<?> cv = (CacheValue<?>) t;
			vd.setData(cv.encode());
		} else {
			vd.setData(JSONObject.toJSONString(t, SerializerFeature.WriteClassName));
		}

		ParserConfig.getGlobalInstance().addAccept(vd.getClassName());
		ParserConfig.getGlobalInstance().addAccept(ValueData.class.getClass().getName());
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		return JSONObject.toJSONString(vd, SerializerFeature.WriteClassName);
	}

	@Override
	public <T> T decode(String str) {
		if (str == null || "".equals(str.trim())) {
			return (T) null;
		}
		ValueData vd = JSONObject.parseObject(str, ValueData.class);
		try {
			ParserConfig.getGlobalInstance().addAccept(vd.getClassName());
			ParserConfig.getGlobalInstance().addAccept(ValueData.class.getClass().getName());
			ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

			Class<?> vClz = Class.forName(vd.getClassName());
			if (CacheValue.class.isAssignableFrom(vClz)) {
				Constructor<?> constructor = vClz.getDeclaredConstructor();
				constructor.setAccessible(true);
				CacheValue<?> cv = (CacheValue<?>) constructor.newInstance();
				cv = cv.decode(vd.getData());
				return (T) cv;
			} else {
				return (T) JSONObject.parseObject(vd.getData(), vClz);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 检查数据类型：
	 * 1、只允许指定的几个类型存入缓存；
	 * 2、CacheValue必须有无参构造器。
	 * @param value
	 */
	public static<T> void validCacheValue(T value) {
		Assert.notNull(value, "[value] is required.");
		CacheClzEnum[] allowedClzs = CacheClzEnum.values();
		Class<?> vClz = value.getClass();
		if (allowedClzs == null || allowedClzs.length <= 0 || vClz == null) {
			throw new RuntimeException("error");
		}

		boolean checked = false;
		for (CacheClzEnum clzEnum: allowedClzs) {
			if (clzEnum.getClz().isAssignableFrom(vClz)) {
				checked = true;
				break;
			}
		}

		if (!checked) {
			log.error("The type of value is {}, but only {} is/are supported.", vClz.getName(), toAllowedClazzStr(allowedClzs));
			throw new RuntimeException("The type of value is not supported by cache.");
		}

		if (CacheValue.class.isAssignableFrom(vClz)) {
			try {
				vClz.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
				log.error("Non-argument constructor is required.");
				throw new RuntimeException("Non-argument constructor is required.");
			}
		}
	}

	private static final String toAllowedClazzStr(CacheClzEnum[] allowedClzs) {
		StringBuilder builder = new StringBuilder("");
		for (CacheClzEnum clzEnum: allowedClzs) {
			builder.append(", " + clzEnum.getClz().getName());
		}
		String str = builder.toString();
		if (str.startsWith(",")) {
			str = str.substring(2);
		}
		return str;
	}
}
