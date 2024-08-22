package com.yuweix.tripod.core.mq.rabbit;


import com.rabbitmq.client.Channel;
import com.yuweix.tripod.core.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;


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
        String spanId = UUID.randomUUID().toString().replace("-", "");
        log.info("SpanId: {}, 接收消息: {}", spanId, JsonUtil.toJSONString(message));
        String body = null;
        try {
            byte[] bytes = message.getBody();
            if (bytes == null || bytes.length <= 0) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            body = new String(bytes);
            if (body.isEmpty()) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            log.info("SpanId: {}, body: {}", spanId, body);
            T t = JsonUtil.parseObject(body, clz);
            process(t);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("SpanId: {}, 消费完成", spanId);
        } catch (Exception e) {
            log.error("SpanId: {}, 消费异常message: {}, Error: {}", spanId, body, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected abstract void process(T t);
}
