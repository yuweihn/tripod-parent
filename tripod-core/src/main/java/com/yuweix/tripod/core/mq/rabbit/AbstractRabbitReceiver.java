package com.yuweix.tripod.core.mq.rabbit;


import com.rabbitmq.client.Channel;
import com.yuweix.tripod.core.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @author yuwei
 * @date 2024-08-17 13:35:35
 */
public abstract class AbstractRabbitReceiver<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractRabbitReceiver.class);

    protected Class<T> clz;

    @SuppressWarnings("unchecked")
    public AbstractRabbitReceiver() {
        this.clz = null;
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            this.clz = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
        }
    }

    @RabbitHandler(isDefault = true)
    public void onMessage(Message message, Channel channel) {
        log.info("接收消息: {}", JsonUtil.toJSONString(message));
        String body = null;
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] bytes = message.getBody();
            if (bytes == null || bytes.length <= 0) {
                channel.basicAck(deliveryTag, false);
                return;
            }
            body = new String(bytes);
            if (body.isEmpty()) {
                channel.basicAck(deliveryTag, false);
                return;
            }
            log.info("body: {}", body);
            T t = JsonUtil.parseObject(body, clz);
            process(t);
            channel.basicAck(deliveryTag, false);
            log.info("消费完成");
        } catch (Exception e) {
            log.error("消费异常message: {}, Error: {}", body, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected abstract void process(T t);
}
