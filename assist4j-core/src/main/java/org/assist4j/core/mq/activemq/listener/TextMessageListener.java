package org.assist4j.core.mq.activemq.listener;


import org.apache.activemq.command.ActiveMQTextMessage;
import org.assist4j.core.mq.activemq.message.IReceiver;
import org.springframework.jms.listener.SessionAwareMessageListener;

import lombok.extern.slf4j.Slf4j;

import javax.jms.Message;
import javax.jms.Session;


/**
 * 文本类消息队列监听器
 * @author yuwei
 */
@Slf4j
public class TextMessageListener implements SessionAwareMessageListener<Message> {
	private IReceiver receiver;

	public void setReceiver(IReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void onMessage(Message message, Session session) {
		if(receiver == null) {
			return;
		}
		try {
			ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
			receiver.receive(msg.getDestination().getPhysicalName(), msg.getText());
		} catch (Exception e) {
			log.error("==>", e);
		}
	}
}

