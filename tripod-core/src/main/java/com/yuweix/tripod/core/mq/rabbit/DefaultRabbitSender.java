package com.yuweix.tripod.core.mq.rabbit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * @author yuwei
 * @date 2024-08-17 13:36:40
 */
public class DefaultRabbitSender implements RabbitSender, Confirmable {
    private static final Logger log = LoggerFactory.getLogger(DefaultRabbitSender.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RabbitTemplate rabbitTemplate;
    /**
     * 从生产者发往交换机的最大可重试次数
     */
    private int maxRetryTimes = 3;

    public DefaultRabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(String exchange, String routeKey, Object message) {
        try {
            MessageProperties properties = new MessageProperties();
            properties.setMessageId(UUID.randomUUID().toString().replace("-", ""));
            Message msg = MessageBuilder.withBody(objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8))
                    .andProperties(properties).build();
            rabbitTemplate.convertAndSend(exchange, routeKey, msg, new RetryData(exchange, routeKey, msg, this));
        } catch (Exception e) {
            log.error("发送消息异常", e);
            throw new RuntimeException("发送消息异常");
        }
    }

    @Override
    public void resend(RetryData retryData) {
        int times = retryData.getRetryTimes() + 1;
        if (times > maxRetryTimes) {
            log.error("超过最大可重试次数！");
            return;
        }
        log.info("重试第{}次", times);
        retryData.setRetryTimes(times);
        rabbitTemplate.convertAndSend(retryData.getExchange(), retryData.getRouteKey(), retryData.getMessage(), retryData);
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }
}
