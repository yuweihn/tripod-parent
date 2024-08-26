package com.yuweix.tripod.core.mq.rabbit;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;


/**
 * @author yuwei
 * @date 2024-08-24 12:27:09
 */
public class ConfirmData extends CorrelationData {
    private String exchange;
    private String routeKey;
    private Message message;

    private Confirmable confirmable;

    /**
     * 当前重试次数
     */
    private int retryTimes = 0;


    public ConfirmData() {
        super();
    }
    public ConfirmData(String exchange, String routeKey, Message message, Confirmable confirmable) {
        super();
        this.exchange = exchange;
        this.routeKey = routeKey;
        this.message = message;
        this.confirmable = confirmable;
    }


    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Confirmable getConfirmable() {
        return confirmable;
    }

    public void setConfirmable(Confirmable confirmable) {
        this.confirmable = confirmable;
    }
}
