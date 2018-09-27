package com.assist4j.data.cache;


import java.lang.reflect.Constructor;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;


/**
 * @author yuwei
 */
public abstract class CacheUtil {
	private static final Logger log = LoggerFactory.getLogger(CacheUtil.class);


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
		if (StringUtils.isEmpty(allowedClzs) || vClz == null) {
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

	public static<T>String objectToString(T value) {
//		validCacheValue(value);

		Class<?> vClz = value.getClass();
		ValueData vd = new ValueData();
		vd.setClassName(vClz.getName());

		if (CacheValue.class.isAssignableFrom(vClz)) {
			CacheValue<?> cv = (CacheValue<?>) value;
			vd.setData(cv.encode());
		} else {
			vd.setData(JSONObject.toJSONString(value, SerializerFeature.WriteClassName));
		}

		ParserConfig.getGlobalInstance().addAccept(vd.getClassName());
		ParserConfig.getGlobalInstance().addAccept(ValueData.class.getClass().getName());
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		return JSONObject.toJSONString(vd, SerializerFeature.WriteClassName);
	}

	@SuppressWarnings("unchecked")
	public static<T>T stringToObject(String str) {
		if (StringUtils.isEmpty(str)) {
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
