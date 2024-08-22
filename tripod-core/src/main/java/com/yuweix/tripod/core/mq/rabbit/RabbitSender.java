package com.yuweix.tripod.core.mq.rabbit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


/**
 * @author yuwei
 * @date 2024-08-17 13:36:40
 */
public class RabbitSender {
    private static final Logger log = LoggerFactory.getLogger(RabbitSender.class);

    private RabbitTemplate rabbitTemplate;

    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(String exchange, String routeKey, Object message) {
        MessageProperties properties = new MessageProperties();
        Message msg = rabbitTemplate.getMessageConverter().toMessage(message, properties);
        rabbitTemplate.convertAndSend(exchange, routeKey, msg);
    }
}
