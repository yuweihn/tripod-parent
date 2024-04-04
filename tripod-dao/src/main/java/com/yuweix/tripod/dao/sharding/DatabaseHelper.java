package com.yuweix.tripod.dao.sharding;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public abstract class DatabaseHelper {
    /**
     * 获取含有{@link Database}注解的参数对象
     */
    public static Object getDatabaseArg(ProceedingJoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        if (params == null || params.length <= 0) {
            return null;
        }
        int idx = -1;
        for (int i = 0, len = params.length; i < len; i++) {
            if (params[i].isAnnotationPresent(Database.class)) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            return null;
        }
        return point.getArgs()[idx];
    }

    /**
     * 检查入参，如果包含某个属性，该属性有{@link Sharding}注解，则返回该属性的值
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
