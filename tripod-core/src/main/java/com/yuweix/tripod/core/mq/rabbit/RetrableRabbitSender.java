package com.yuweix.tripod.core.mq.rabbit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


/**
 * @author yuwei
 * @date 2024-08-17 13:36:40
 */
public abstract class RetrableRabbitSender implements RabbitSender {
    private static final Logger log = LoggerFactory.getLogger(RetrableRabbitSender.class);

    private RabbitTemplate rabbitTemplate;

    /**
     * 最大可重试次数
     */
    private int maxRetryTimes = 3;


    public RetrableRabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void resend(RetryData retryData) {
        int times = retryData.getRetryTimes() + 1;
        if (times > maxRetryTimes) {
            log.warn("超过最大可重试次数！");
            return;
        }
        retryData.setRetryTimes(times);
        rabbitTemplate.convertAndSend(retryData.getExchange(), retryData.getRouteKey(), retryData.getMessage(), retryData);
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }
}
