package com.assist4j.core.mail;


import com.assist4j.core.Response;


/**
 * 邮件发送接口
 * @author yuwei
 */
public interface EmailSender<T> {
	Response<Void> send(T mail);
}
