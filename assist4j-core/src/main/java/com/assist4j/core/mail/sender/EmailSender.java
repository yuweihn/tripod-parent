package com.assist4j.core.mail.sender;


import com.assist4j.core.Response;
import com.assist4j.core.mail.Mail;


/**
 * 邮件发送接口
 * @author yuwei
 */
public interface EmailSender {
	Response<Void> send(Mail mail);
}
