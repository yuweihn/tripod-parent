package com.assist4j.core.springboot;


import com.assist4j.core.mail.DefaultEmailSender;
import com.assist4j.core.mail.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


/**
 * @author yuwei
 */
public class SmtpConf {

	@Bean(name = "defaultEmailSender")
	public EmailSender defaultSender(@Value("${smtp.server}") String server
			, @Value("${smtp.ssl.port}") int sslPort
			, @Value("${smtp.auth}") boolean auth
			, @Value("${smtp.user}") String user
			, @Value("${smtp.password}") String password) {
		DefaultEmailSender defaultSender = new DefaultEmailSender();
		defaultSender.setServer(server);
		defaultSender.setSslPort(sslPort);
		defaultSender.setAuth(auth);
		defaultSender.setUser(user);
		defaultSender.setPassword(password);
		return defaultSender;
	}
}
