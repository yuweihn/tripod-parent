package com.yuweix.tripod.dao.sharding;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public abstract class ShardAopUtil {
    public static Object getAnnotationArgVal(ProceedingJoinPoint point
            , Class<? extends Annotation> paramClz, Class<? extends Annotation> fieldClz) {
        Object argObj = getAnnotationArg(point, paramClz);
        if (argObj == null) {
            return null;
        }
        return getAnnotationArgVal(argObj, fieldClz);
    }

    /**
     * 获取含有指定注解的参数对象
     */
    public static Object getAnnotationArg(ProceedingJoinPoint point, Class<? extends Annotation> clz) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Parameter[] params = method.getParameters();
        if (params == null || params.length <= 0) {
            return null;
        }
        int idx = -1;
        for (int i = 0, len = params.length; i < len; i++) {
            if (params[i].isAnnotationPresent(clz)) {
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
     * 检查入参对象所属类型中含有指定注解的属性，有则返回该属性的值，如果没有则返回入参本身的值。
     */
    public static Object getAnnotationArgVal(Object obj, Class<? extends Annotation> clz) {
        if (obj == null) {
            return null;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length <= 0) {
            return obj;
        }
        for (Field field: fields) {
            if (field.isAnnotationPresent(clz)) {
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
