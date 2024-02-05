package com.yuweix.tripod.boot.permission;


import com.yuweix.tripod.permission.springboot.PermissionConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.permission.enabled")
@Import({PermissionConf.class})
public class PermissionAutoConfiguration {

}
