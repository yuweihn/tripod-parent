package com.yuweix.tripod.core.mq.rabbit;


import org.springframework.amqp.rabbit.connection.CorrelationData;


public interface RabbitCallback {
    void call(CorrelationData correlationData, boolean ack, String cause);
}
