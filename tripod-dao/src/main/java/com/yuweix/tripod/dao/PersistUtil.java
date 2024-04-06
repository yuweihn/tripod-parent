package com.yuweix.tripod.dao;


import com.yuweix.tripod.dao.sharding.*;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author yuwei
 */
public abstract class PersistUtil {
	private static SoftReference<Map<String, String>> TABLE_NAME_REF;
	private static final Object tableNameLock = new Object();

	private static SoftReference<Map<String, String>> SELECT_SQL_REF;
	private static final Object selectSqlLock = new Object();

	private static SoftReference<Map<String, String>> SELECT_SQL_WITH_TABLE_ALIAS_REF;
	private static final Object selectSqlWithAliasLock = new Object();

	private static SoftReference<Map<String, List<FieldCol>>> PERSIST_FIELD_REF;
	private static final Object persistFieldLock = new Object();

	private static SoftReference<Map<Class<? extends Strategy>, Strategy>> SHARD_STRATEGY_REF;
	private static final Object shardStrategyLock = new Object();

	private static SoftReference<Map<Class<?>, FieldCol>> CLASS_PK_FIELD_REF;
	private static final Object classPkFieldLock = new Object();

	private static SoftReference<Map<Class<?>, FieldCol>> CLASS_SHARDING_FIELD_REF;
	private static final Object classShardingFieldLock = new Object();


	/**
	 * 驼峰转下划线
	 * @param str
	 * @return
	 */
	private static String toUnderline(String str) {
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(str);
		StringBuffer buf = new StringBuffer(str);
		if (!matcher.find()) {
			return buf.toString();
		}
		buf = new StringBuffer();
		matcher.appendReplacement(buf, "_" + matcher.group(0).toLowerCase());
		matcher.appendTail(buf);
		String res = toUnderline(buf.toString());
		if (res.startsWith("_")) {
			res = res.substring(1);
		}
		return res;
	}

	private static Map<String, String> getTableNameMap() {
		Map<String, String> map = null;
		if (TABLE_NAME_REF == null || (map = TABLE_NAME_REF.get()) == null) {
			synchronized (tableNameLock) {
				if (TABLE_NAME_REF == null || (map = TABLE_NAME_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					TABLE_NAME_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	/**
	 * 根据持久化类获取对应的关系表表名。
	 * @param clz
	 * @return
	 */
	public static String getTableName(Class<?> clz) {
		String className = clz.getName();
		Map<String, String> map = getTableNameMap();
		String tableName = map.get(className);
		if (tableName == null) {
			Table table = clz.getAnnotation(Table.class);
			if (table != null && !"".equals(table.name().trim())) {
				tableName = table.name().trim();
			} else {
				tableName = toUnderline(clz.getSimpleName());
				if (tableName == null || "".equals(tableName)) {
					throw new RuntimeException("Table name is not found.");
				}
			}
			map.put(className, tableName);
		}
		return tableName;
	}

	private static Map<String, String> getSelectSqlMap() {
		Map<String, String> map = null;
		if (SELECT_SQL_REF == null || (map = SELECT_SQL_REF.get()) == null) {
			synchronized (selectSqlLock) {
				if (SELECT_SQL_REF == null || (map = SELECT_SQL_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					SELECT_SQL_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	/**
	 * 根据持久化类获取对应的所有需要持久化的属性SQL。
	 * eg：id as id, user_name as userName, create_time as createTime
	 * @param clz
	 * @return
	 */
	public static String getAllColumnSql(Class<?> clz) {
		String className = clz.getName();
		Map<String, String> map = getSelectSqlMap();
		String selectSql = map.get(className);
		if (selectSql == null) {
			StringBuilder builder = new StringBuilder("");
			List<FieldCol> fcList = getPersistFieldList(clz);
			for (int i = 0, size = fcList.size(); i < size; i++) {
				FieldCol fc = fcList.get(i);
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(fc.getColumnName()).append(" as ").append(fc.getField().getName());
			}
			selectSql = builder.toString();
			map.put(className, selectSql);
		}
		return selectSql;
	}

	private static Map<String, String> getSelectSqlWithTableAliasMap() {
		Map<String, String> map = null;
		if (SELECT_SQL_WITH_TABLE_ALIAS_REF == null || (map = SELECT_SQL_WITH_TABLE_ALIAS_REF.get()) == null) {
			synchronized (selectSqlWithAliasLock) {
				if (SELECT_SQL_WITH_TABLE_ALIAS_REF == null || (map = SELECT_SQL_WITH_TABLE_ALIAS_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					SELECT_SQL_WITH_TABLE_ALIAS_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	/**
	 * eg：a.id as id, a.user_name as userName, a.create_time as createTime
	 * @param clz
	 * @param tableAlias
	 * @return
	 */
	public static String getAllColumnSql(Class<?> clz, String tableAlias) {
		String key = clz.getName() + "_" + tableAlias;
		Map<String, String> map = getSelectSqlWithTableAliasMap();
		String selectSql = map.get(key);
		if (selectSql == null) {
			StringBuilder builder = new StringBuilder("");
			List<FieldCol> fcList = getPersistFieldList(clz);
			for (int i = 0, size = fcList.size(); i < size; i++) {
				FieldCol fc = fcList.get(i);
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(tableAlias).append(".").append(fc.getColumnName()).append(" as ").append(fc.getField().getName());
			}
			selectSql = builder.toString();
			map.put(key, selectSql);
		}
		return selectSql;
	}

	private static Map<String, List<FieldCol>> getPersistFieldMap() {
		Map<String, List<FieldCol>> map = null;
		if (PERSIST_FIELD_REF == null || (map = PERSIST_FIELD_REF.get()) == null) {
			synchronized (persistFieldLock) {
				if (PERSIST_FIELD_REF == null || (map = PERSIST_FIELD_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					PERSIST_FIELD_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	/**
	 * 根据持久化类获取对应的所有需要持久化的属性集合
	 * @param clz
	 * @return
	 */
	public static List<FieldCol> getPersistFieldList(Class<?> clz) {
		String className = clz.getName();
		Map<String, List<FieldCol>> map = getPersistFieldMap();
		List<FieldCol> fcList = map.get(className);
		if (fcList == null) {
			fcList = getPersistFieldList0(clz);
			map.put(className, fcList);
		}
		return fcList;
	}

	/**
	 * Gets all fields of the given class and its parents (if any).
	 * @return
	 */
	private static List<FieldCol> getPersistFieldList0(Class<?> clz) {
		final List<Field> allFields = new ArrayList<>();
		Class<?> currentClass = clz;
		while (currentClass != null) {
			allFields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
			currentClass = currentClass.getSuperclass();
		}

		List<FieldCol> list = new ArrayList<>();
		for (Field field: allFields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}

			String colName = column.name();
			if ("".equals(colName.trim())) {
				colName = toUnderline(field.getName());
			}
			if (colName == null || "".equals(colName.trim())) {
				continue;
			}
			list.add(new FieldCol(colName.trim(), field));
		}
		return list;
	}

	public static class FieldCol {
		private String columnName;
		private Field field;

		public FieldCol(String columnName, Field field) {
			this.columnName = columnName;
			this.field = field;
		}

		public String getColumnName() {
			return columnName;
		}
		public Field getField() {
			return field;
		}
	}

	public static Object getFieldValue(Field field, Object t) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			return field.get(t);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<Class<? extends Strategy>, Strategy> getShardStrategyMap() {
		Map<Class<? extends Strategy>, Strategy> map = null;
		if (SHARD_STRATEGY_REF == null || (map = SHARD_STRATEGY_REF.get()) == null) {
			synchronized (shardStrategyLock) {
				if (SHARD_STRATEGY_REF == null || (map = SHARD_STRATEGY_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					SHARD_STRATEGY_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	public static String getShardingIndex(Sharding sharding, String tableName, Object shardingVal) {
		if (sharding == null) {
			return null;
		}
		Class<? extends Strategy> strategyClz = sharding.strategy();
		Map<Class<? extends Strategy>, Strategy> map = getShardStrategyMap();
		Strategy strategy = map.get(strategyClz);
		if (strategy == null) {
			try {
				strategy = strategyClz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			map.put(strategyClz, strategy);
		}
		return getShardingIndex(strategy, tableName, shardingVal);
	}
	public static String getShardingIndex(Strategy strategy, String tableName, Object shardingVal) {
		if (strategy == null || shardingVal == null) {
			return null;
		}
		return strategy.getShardingIndex(tableName, shardingVal);
	}
	public static String getShardingDatabaseIndex(Strategy strategy, String databaseName, String tableName, Object shardingVal) {
		if (strategy == null || shardingVal == null) {
			return null;
		}
		return strategy.getShardingDatabaseIndex(databaseName, tableName, shardingVal);
	}

	public static <T>String getPhysicalTableName(Class<T> entityClass, Object shardingVal) {
		String tbName = getTableName(entityClass);
		List<FieldCol> fcList = getPersistFieldList(entityClass);
		for (FieldCol fc: fcList) {
			Field field = fc.getField();
			Sharding sharding = field.getAnnotation(Sharding.class);
			if (sharding != null) {
				String shardingIndex = getShardingIndex(sharding, tbName, shardingVal);
				return tbName + "_" + shardingIndex;
			}
		}
		return tbName;
	}

	public static <T>String getPhysicalTableName(T t) {
		Class<?> entityClass = t.getClass();
		String tbName = getTableName(entityClass);
		List<FieldCol> fcList = getPersistFieldList(entityClass);
		for (FieldCol fc: fcList) {
			Field field = fc.getField();
			Sharding sharding = field.getAnnotation(Sharding.class);
			if (sharding != null) {
				String shardingIndex = getShardingIndex(sharding, tbName, getFieldValue(field, t));
				return tbName + "_" + shardingIndex;
			}
		}
		return tbName;
	}

	private static Map<Class<?>, FieldCol> getClassPkFieldMap() {
		Map<Class<?>, FieldCol> map = null;
		if (CLASS_PK_FIELD_REF == null || (map = CLASS_PK_FIELD_REF.get()) == null) {
			synchronized (classPkFieldLock) {
				if (CLASS_PK_FIELD_REF == null || (map = CLASS_PK_FIELD_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					CLASS_PK_FIELD_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	public static FieldCol getPKFieldCol(Class<?> clz) {
		Map<Class<?>, FieldCol> map = getClassPkFieldMap();
		FieldCol fc = map.get(clz);

		if (fc == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					Column col = field.getAnnotation(Column.class);
					fc = new FieldCol(col == null ? field.getName() : col.name(), field);
					map.put(clz, fc);
					break;
				}
			}
		}
		return fc;
	}

	private static Map<Class<?>, FieldCol> getClassShardingFieldMap() {
		Map<Class<?>, FieldCol> map = null;
		if (CLASS_SHARDING_FIELD_REF == null || (map = CLASS_SHARDING_FIELD_REF.get()) == null) {
			synchronized (classShardingFieldLock) {
				if (CLASS_SHARDING_FIELD_REF == null || (map = CLASS_SHARDING_FIELD_REF.get()) == null) {
					map = new ConcurrentHashMap<>();
					CLASS_SHARDING_FIELD_REF = new SoftReference<>(map);
				}
			}
		}
		return map;
	}
	public static FieldCol getShardingFieldCol(Class<?> clz) {
		Map<Class<?>, FieldCol> map = getClassShardingFieldMap();
		FieldCol fc = map.get(clz);

		if (fc == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Sharding sAnn = field.getAnnotation(Sharding.class);
				if (sAnn != null) {
					Column col = field.getAnnotation(Column.class);
					fc = new FieldCol(col == null ? field.getName() : col.name(), field);
					map.put(clz, fc);
					break;
				}
			}
		}
		return fc;
	}
}
