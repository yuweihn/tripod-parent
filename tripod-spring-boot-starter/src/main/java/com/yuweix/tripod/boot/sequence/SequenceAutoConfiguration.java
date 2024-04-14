package com.yuweix.tripod.boot.sequence;


import com.yuweix.tripod.sequence.springboot.SequenceConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.sequence.enabled")
@Import({SequenceConf.class})
public class SequenceAutoConfiguration {

}
