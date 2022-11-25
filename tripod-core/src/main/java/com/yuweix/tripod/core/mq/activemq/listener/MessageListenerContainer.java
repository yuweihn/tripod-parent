package com.yuweix.tripod.core.mq.activemq.listener;


import jakarta.annotation.Resource;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import jakarta.jms.ConnectionFactory;


/**
 * 队列监听容器
 * @author yuwei
 */
public class MessageListenerContainer extends DefaultMessageListenerContainer {

	public void setQueueName(String queueName) {
		setDestinationName(queueName);
	}

	@Override
	@Resource
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		super.setConnectionFactory(connectionFactory);
	}

	@Override
	@Resource
	public void setMessageListener(Object messageListener) {
		super.setMessageListener(messageListener);
	}
}
