package com.yuweix.tripod.boot.dao;


import com.yuweix.tripod.dao.springboot.HibernateConf;
import com.yuweix.tripod.sequence.springboot.SequenceConf;
import com.yuweix.tripod.sharding.springboot.ShardingConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.dao.hibernate.enabled")
@Import({ShardingConf.class, HibernateConf.class, SequenceConf.class})
public class HibernateAutoConfiguration {

}
