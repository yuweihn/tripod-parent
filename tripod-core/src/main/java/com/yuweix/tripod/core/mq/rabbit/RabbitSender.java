package com.yuweix.tripod.core.mq.rabbit;


/**
 * @author yuwei
 * @date 2024-08-17 13:36:40
 */
public interface RabbitSender {
    void sendMessage(String exchange, String routeKey, Object message);
}
