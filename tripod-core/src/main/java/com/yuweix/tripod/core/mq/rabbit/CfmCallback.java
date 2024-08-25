package com.yuweix.tripod.core.mq.rabbit;


import org.springframework.amqp.rabbit.connection.CorrelationData;


/**
 * 生产者到交换机的消息确认
 * @author yuwei
 **/
public interface CfmCallback {
    void call(CorrelationData correlationData, boolean ack, String cause);
}
