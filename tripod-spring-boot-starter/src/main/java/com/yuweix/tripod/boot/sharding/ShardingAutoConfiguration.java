package com.yuweix.tripod.boot.sharding;


import com.yuweix.tripod.sharding.springboot.ShardingConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.sharding.enabled")
@Import({ShardingConf.class})
public class ShardingAutoConfiguration {

}
