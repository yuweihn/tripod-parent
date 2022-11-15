package com.yuweix.tripod.boot.dao;


import com.yuweix.tripod.dao.sharding.ShardingConf;
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
