package com.yuweix.tripod.schedule;


import com.yuweix.tripod.schedule.base.AbstractTask;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * @author yuwei
 */
@Aspect
public class ScheduleAspect {
    @Pointcut("@annotation(com.yuweix.tripod.schedule.Executable)")
    public void executable() {

    }

    @Around("executable()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        if (!(target instanceof AbstractTask)) {
            joinPoint.proceed();
        } else {
            ((AbstractTask) target).start();
        }
    }
}
