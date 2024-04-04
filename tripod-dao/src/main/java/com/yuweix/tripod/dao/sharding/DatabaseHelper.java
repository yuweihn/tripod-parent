package com.yuweix.tripod.dao.sharding;


import java.lang.reflect.Field;


public abstract class DatabaseHelper {
    /**
     * 检查入参，如果包含某个属性，该属性有Sharding注解，则返回该属性的值
     * @param obj
     * @return
     */
    public static Object parse(Object obj) {
        if (obj == null) {
            return null;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length <= 0) {
            return obj;
        }
        for (Field field: fields) {
            if (field.isAnnotationPresent(Sharding.class)) {
                try {
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return obj;
    }
}
