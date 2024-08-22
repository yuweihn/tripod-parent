package com.yuweix.tripod.boot.core;


import com.yuweix.tripod.core.springboot.RabbitConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.mq.rabbit.enabled")
@Import({RabbitConf.class})
public class RabbitAutoConfiguration {

}
