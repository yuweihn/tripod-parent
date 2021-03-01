package com.yuweix.assist4j.session;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 * Session的相关属性键值对储存Bean.
 * @author yuwei
 */
public class SessionAttribute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private boolean newBuild;
	private Date lastAccessTime;
	private Date createTime;
	private String repeatKey;
	private Object repeatValue;
	private Map<String, Object> attributes;


	public boolean isEmpty() {
		return attributes == null || attributes.size() <= 0;
	}

	public SessionAttribute() {
		attributes = new HashMap<String, Object>();
	}
	public SessionAttribute(Date createTime) {
		this();
		this.createTime = createTime;
	}

	public void putAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public Object removeAttribute(String name) {
		return attributes.remove(name);
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public boolean isNewBuild() {
		return newBuild;
	}

	public void setNewBuild(boolean newBuild) {
		this.newBuild = newBuild;
	}

	public String getRepeatKey() {
		return repeatKey;
	}

	public void setRepeatKey(String repeatKey) {
		this.repeatKey = repeatKey;
	}

	public Object getRepeatValue() {
		return repeatValue;
	}

	public void setRepeatValue(Object repeatValue) {
		this.repeatValue = repeatValue;
	}


	public static String encode(SessionAttribute attr) {
		if (attr == null) {
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newBuild", attr.newBuild);
		map.put("lastAccessTime", attr.lastAccessTime);
		map.put("createTime", attr.createTime);

		if (!attr.isEmpty()) {
			map.put("attributes", attr.attributes);
		}
		if (attr.repeatKey != null && !"".equals(attr.repeatKey) && attr.repeatValue != null && !"".equals(attr.repeatValue.toString())) {
			map.put("repeatKey", attr.repeatKey);
			map.put("repeatValue", attr.repeatValue);
		}
		return JSONObject.toJSONString(map, SerializerFeature.WriteClassName);
	}

	@SuppressWarnings("unchecked")
	public static SessionAttribute decode(String value) {
		if (!ParserConfig.getGlobalInstance().isAutoTypeSupport()) {
			ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		}
		Map<String, Object> map = JSONObject.parseObject(value, new TypeReference<Map<String, Object>>() {});
		
		if (map == null) {
			return null;
		}

		Object newBuildObj = map.get("newBuild");
		Object lastAccessTimeObj = map.get("lastAccessTime");
		Object createTimeObj = map.get("createTime");
		Object attributesObj = map.get("attributes");
		Object repeatKeyObj = map.get("repeatKey");
		Object repeatValueObj = map.get("repeatValue");
		
		SessionAttribute attr = new SessionAttribute();
		attr.newBuild = Boolean.parseBoolean(newBuildObj == null ? "false" : newBuildObj.toString());
		attr.lastAccessTime = (Date) lastAccessTimeObj;
		attr.createTime = (Date) createTimeObj;
		attr.attributes = (Map<String, Object>) attributesObj;
		attr.repeatKey = (String) repeatKeyObj;
		attr.repeatValue = repeatValueObj;
		return attr;
	}
	
	/**
	 * 按指定格式格式化日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	private static String formatDate(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}
	
	/**
	 * 按指定格式解析日期
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	private static Date parseDate(String dateStr, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
