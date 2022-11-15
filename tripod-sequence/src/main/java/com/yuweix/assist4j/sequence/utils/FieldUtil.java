package com.yuweix.tripod.sequence.utils;


import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yuwei
 */
public abstract class FieldUtil {
	private static SoftReference<Map<String, List<Field>>> FIELD_REF;
	private static final Object fieldLock = new Object();

	private static Map<String, List<Field>> getFieldMap() {
		Map<String, List<Field>> map = null;
		if (FIELD_REF == null || (map = FIELD_REF.get()) == null) {
			synchronized (fieldLock) {
				if (FIELD_REF == null || (map = FIELD_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					FIELD_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	public static List<Field> getAllFieldsList(Class<?> clz) {
		String className = clz.getName();
		Map<String, List<Field>> map = getFieldMap();
		List<Field> fList = map.get(className);
		if (fList == null) {
			fList = getAllFieldsList0(clz);
			map.put(className, fList);
		}
		return fList;
	}
	/**
	 * Gets all fields of the given class and its parents (if any).
	 * @return
	 */
	private static List<Field> getAllFieldsList0(Class<?> clz) {
		final List<Field> allFields = new ArrayList<>();
		Class<?> currentClass = clz;
		while (currentClass != null) {
			allFields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
			currentClass = currentClass.getSuperclass();
		}
		return allFields;
	}
}
