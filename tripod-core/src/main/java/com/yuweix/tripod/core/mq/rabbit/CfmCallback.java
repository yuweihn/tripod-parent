package com.yuweix.tripod.core.mq.rabbit;


import org.springframework.amqp.rabbit.connection.CorrelationData;


/**
 * @author yuwei
 **/
public interface CfmCallback {
    void call(CorrelationData correlationData, boolean ack, String cause);
}
