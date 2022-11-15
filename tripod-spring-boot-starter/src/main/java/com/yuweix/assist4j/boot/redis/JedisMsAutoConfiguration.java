package com.yuweix.tripod.boot.redis;


import com.yuweix.tripod.data.springboot.jedis.JedisMsConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.jedis.ms.enabled")
@Import({JedisMsConf.class})
public class JedisMsAutoConfiguration {

}
