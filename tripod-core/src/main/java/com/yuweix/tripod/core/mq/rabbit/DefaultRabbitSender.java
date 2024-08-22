package com.yuweix.tripod.core.mq.rabbit;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


/**
 * @author yuwei
 * @date 2024-08-17 13:36:40
 */
public class DefaultRabbitSender implements RabbitSender {
    private RabbitTemplate rabbitTemplate;

    public DefaultRabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(String exchange, String routeKey, Object message) {
        MessageProperties properties = new MessageProperties();
        Message msg = rabbitTemplate.getMessageConverter().toMessage(message, properties);
        rabbitTemplate.convertAndSend(exchange, routeKey, msg);
    }
}
