package com.yuweix.tripod.boot.schedule;


import com.yuweix.tripod.schedule.springboot.ScheduleConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.schedule.enabled")
@Import({ScheduleConf.class})
public class ScheduleAutoConfiguration {

}
