package com.yuweix.tripod.dao.sharding;


import java.lang.reflect.Field;
import java.util.List;


/**
 * @author yuwei
 */
public class ShardUtil {

    public static <T>String getPhysicalTableName(Class<T> entityClass, Object shardingVal) {
        String tbName = getTableName(entityClass);
        List<FieldColumn> fcList = getPersistFieldList(entityClass);
        for (AbstractProvider.FieldColumn fc: fcList) {
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
        List<FieldColumn> fcList = getPersistFieldList(entityClass);
        for (FieldColumn fc: fcList) {
            Field field = fc.getField();
            Sharding sharding = field.getAnnotation(Sharding.class);
            if (sharding != null) {
                String shardingIndex = getShardingIndex(sharding, tbName, getFieldValue(field, t));
                return tbName + "_" + shardingIndex;
            }
        }
        return tbName;
    }
}
