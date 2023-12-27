package com.yuweix.tripod.boot.datasource;


import com.yuweix.tripod.dao.springboot.HibernateConf;
import com.yuweix.tripod.sequence.springboot.SequenceConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.hibernate.enabled")
@Import({HibernateConf.class, SequenceConf.class})
public class HibernateAutoConfiguration {

}
