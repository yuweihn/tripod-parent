package com.yuweix.tripod.dao.datasource;


import com.yuweix.tripod.sharding.annotation.ShardingDataSource;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;


/**
 * @author yuwei
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ShardingDataSource("")
public @interface DataSource {
    @AliasFor(annotation = ShardingDataSource.class)
    String value();
}
