package com.yuweix.tripod.boot.redis;


import com.yuweix.tripod.data.springboot.jedis.JedisClusterConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.jedis.cluster.enabled")
@Import({JedisClusterConf.class})
public class JedisClusterAutoConfiguration {

}
