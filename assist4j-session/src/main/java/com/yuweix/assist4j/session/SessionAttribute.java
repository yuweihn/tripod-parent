package com.yuweix.assist4j.session;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
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


	public SessionAttribute() {
		Date now = Calendar.getInstance().getTime();
		this.createTime = now;
		this.lastAccessTime = now;
		this.newBuild = true;
		this.attributes = new HashMap<>();
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
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

	public void putAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public Object removeAttribute(String name) {
		return attributes.remove(name);
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public void clear() {
		if (attributes == null) {
			return;
		}
		attributes.clear();
	}


	public static String serialize(SessionAttribute attr) {
		if (attr == null) {
			return null;
		}

		return JSONObject.toJSONString(attr, SerializerFeature.WriteClassName);
	}

	public static SessionAttribute deserialize(String value) {
		if (!ParserConfig.getGlobalInstance().isAutoTypeSupport()) {
			ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		}
		return JSONObject.parseObject(value, new TypeReference<SessionAttribute>() {});
	}
}
