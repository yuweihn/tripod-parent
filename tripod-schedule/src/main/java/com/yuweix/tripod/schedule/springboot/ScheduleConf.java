package com.yuweix.tripod.schedule.springboot;


import com.yuweix.tripod.schedule.ScheduleAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


/**
 * @author yuwei
 */
public class ScheduleConf {
	@ConditionalOnMissingBean(ScheduleAspect.class)
	@Bean(name = "scheduleAspect")
	public ScheduleAspect scheduleAspect() {
		return new ScheduleAspect();
	}
}
