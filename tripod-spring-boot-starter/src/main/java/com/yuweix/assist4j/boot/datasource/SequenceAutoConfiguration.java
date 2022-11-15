package com.yuweix.assist4j.boot.datasource;


import com.yuweix.assist4j.sequence.springboot.SequenceConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.sequence.enabled")
@Import({SequenceConf.class})
public class SequenceAutoConfiguration {

}
