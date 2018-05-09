package com.assist4j.core;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public abstract class BeanUtil {
	private static final String[] CHAR_ARRAY = {"A", "B", "C", "D", "E", "F", "G"
											, "H", "I", "J", "K", "L", "M", "N"
											, "O", "P", "Q", "R", "S", "T"
											, "U", "V", "W", "X", "Y", "Z"
											, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};



	/**
	 * 阿尔法排序后的键值对。
	 * appkey=miutest[conn]force=true[conn]order_id=YC1603290001101T[conn]tp_customer_phone=17717601007
	 */
	public static String getAlphaString(Map<String, ? extends Object> map, String conn) {
		return getAlphaString(map, conn, null);
	}

	public static String getAlphaString(Map<String, ? extends Object> map, String conn, String charset) {
		Assert.notNull(conn, "[conn] is required.");
		if(map == null || map.isEmpty()) {
			return "";
		}

		Set<String> keys = map.keySet();
		List<String> list = new ArrayList<String>();
		for (String k: keys) {
			Object v = map.get(k);
			if(v == null) {
				v = "";
			} else if(charset != null) {
				try {
					k = URLEncoder.encode(k, charset);
					v = URLEncoder.encode(v.toString(), charset);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}

			list.add(k + "=" + v);
		}

		Collections.sort(list);
		StringBuilder builder = new StringBuilder("");
		for (String kv: list) {
			builder.append(kv).append(conn);
		}

		if (conn.length() > 0) {
			builder.delete(builder.length() - conn.length(), builder.length());
		}
		return builder.toString();
	}


	/**
	 * 取得指定长度的随机码
	 * @param length
	 * @return
	 */
	public static String getRandCode(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < length; i++) {
			builder.append(CHAR_ARRAY[random.nextInt(CHAR_ARRAY.length)]);
		}
		return builder.toString();
	}


	/**
	 * 是否是Email
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || "".equals(email.trim())) {
			return false;
		}
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email.trim());
		return m.find();
	}

	/**
	 * 是否是手机号码
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		if (mobile == null || "".equals(mobile.trim())) {
			return false;
		}
		Pattern p = Pattern.compile("^[1][3-9]\\d{9}$");
		Matcher m = p.matcher(mobile.trim());
		return m.find();
	}

	/**
	 * 是否是电话号码
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {
		if (phone == null || "".equals(phone.trim())) {
			return false;
		}
		Pattern p = Pattern.compile("^[\\d]{5,20}$");
		Matcher m = p.matcher(phone.trim());
		return m.find();
	}

	/**
	 * 按指定长度截取字符串(一个汉字相当于两个字符)，超过的以省略号代替
	 * @param str
	 * @param len
	 * @return
	 */
	public static String cutString(String str, int len) {
		if(str == null || str.length() <= 0 || len <= 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder("");
		int count = 0;
		for(int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			count += isChineseChar(c) ? 2 : 1;

			if(count > len) {
				builder.append("......");
				break;
			}
			builder.append(c);
		}

		return builder.toString();
	}

	/**
	 * 以纯文本形式显示时的过滤逻辑
	 * @param str
	 * @return
	 */
	public static String escape(String str) {
		if(str == null || str.length() <= 0) {
			return "";
		}

		return str.trim().replace("<", "&lt;").replace(">", "&gt;").replace("\"", "\\\"").replace("\r\n", "<br/>").replace("\n", "<br/>");
	}

	/**
	 * 需要显示html标签时的过滤逻辑
	 * @param str
	 * @return
	 */
	public static String escape2(String str) {
		if(str == null || str.length() <= 0) {
			return "";
		}

		return str.trim().replace("\"", "\\\"").replace("\r\n", "<br/>").replace("\n", "<br/>");
	}

	/**
	 * 是否是中文字符
	 * @param c
	 * @return
	 */
	public static boolean isChineseChar(char c) {
		return Character.isLetter(c) && c > 255;
	}

	/**
	 * 获取域名
	 * 如 http://www.yuweix.com/v1/file
	 * 返回 http://www.yuweix.com
	 * @param url
	 * @return
	 */
	public static String getDomainUrl(String url) {
		try {
			StringBuilder builder = new StringBuilder("");
			URL url0 = new URL(url);
			builder.append(url0.getProtocol()).append("://").append(url0.getHost());
			int port = url0.getPort();
			if(port != Constant.DEFAULT_HTTP_PORT && port != Constant.DEFAULT_HTTPS_PORT && port > 0) {
				builder.append(":").append(port);
			}
			return builder.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static<T> T copyProperties(Object source, Class<T> targetClass) {
		if(source == null) {
			return null;
		}

		try {
			Constructor<T> constructor = targetClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			T targetObj = constructor.newInstance();
			copyProperties(source, targetObj);
			return targetObj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static<T> void copyProperties(Object source, T target) {
		if(source == null || target == null) {
			return;
		}

		try {
			BeanUtils.copyProperties(source, target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static<T> String join(T[] arr, String separator) {
		if(arr == null || arr.length <= 0) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		for(T t: arr) {
			builder.append(separator).append(t.toString());
		}
		builder.delete(0, separator.length());
		return builder.toString();
	}
}
