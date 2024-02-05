package com.yuweix.tripod.boot.redis;


import com.yuweix.tripod.data.springboot.lettuce.LettuceClusterConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.lettuce.cluster.enabled")
@Import({LettuceClusterConf.class})
public class LettuceClusterAutoConfiguration {

}
