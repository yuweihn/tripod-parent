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
    @Pointcut("@annotation(com.yuweix.tripod.schedule.Schedule)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        if (!(target instanceof AbstractTask)) {
            joinPoint.proceed();
            return;
        }

        AbstractTask task = (AbstractTask) target;
        task.execute();
    }
}
