package com.yuweix.assist4j.boot.exception;


import com.alibaba.fastjson.JSON;
import com.yuweix.assist4j.core.Response;
import com.yuweix.assist4j.core.exception.ExceptionHandler;
import com.yuweix.assist4j.core.exception.ExceptionViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.exception.enabled")
public class ExceptionAutoConfiguration {
	@Configuration
	@ConditionalOnProperty(name = "assist4j.boot.exception.handler.enabled", matchIfMissing = true)
	protected static class ErrorControllerConfiguration {
		@Value("${assist4j.boot.exception.errorCode:}")
		private String errorCode;

		@Controller
		public class ErrorController {
			@RequestMapping(value = { "/error", "/error/**" })
			@ResponseBody
			public String toErrorPage(HttpServletResponse response) {
				int status = response.getStatus();
				HttpStatus httpStatus = HttpStatus.valueOf(status);

				Response<String, Void> resp = new Response<String, Void>(errorCode == null || "".equals(errorCode) ? "" + status : errorCode
						, httpStatus.getReasonPhrase() + "[" + status + "]");
				return JSON.toJSONString(resp);
			}
		}

		@Bean
		@ConditionalOnMissingBean(ExceptionViewResolver.class)
		public ExceptionViewResolver exceptionViewResolver() {
			return new ExceptionViewResolver() {
				@SuppressWarnings("unchecked")
				@Override
				public ModelAndView createView(String content) {
					AbstractView view = new AbstractView() {
						@Override
						protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest req, HttpServletResponse resp) throws Exception {
							resp.setContentType("application/json; charset=" + StandardCharsets.UTF_8);
							ServletOutputStream out = resp.getOutputStream();
							out.write(JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8));
							out.flush();
						}
					};
					String text = JSON.toJSONString(
							new Response<String, Void>(errorCode == null || "".equals(errorCode) ? "500" : errorCode, content));
					Map<String, Object> attributes = JSON.parseObject(text, Map.class);
					view.setAttributesMap(attributes);
					return new ModelAndView(view);
				}
			};
		}
	}

	@Bean
	@ConditionalOnMissingBean(ClassMessagePair.class)
	@ConfigurationProperties(prefix = "assist4j.boot.exception", ignoreUnknownFields = true)
	public ClassMessagePair classMessagePair() {
		return new ClassMessagePair() {
			private Map<String, String> map = new HashMap<String, String>();

			@Override
			public Map<String, String> getDefaultMessage() {
				return map;
			}
		};
	}

	@Bean
	@ConditionalOnMissingBean(ExceptionHandler.class)
	public ExceptionHandler exceptionHandler(ClassMessagePair classMessagePair, ExceptionViewResolver viewResolver,
			@Value("${assist4j.boot.exception.showExceptionName:false}") boolean showExceptionName) {
		Map<Class<?>, String> errorMsgMap = new HashMap<Class<?>, String>();

		Map<String, String> classMessageMap = classMessagePair.getDefaultMessage();
		if (classMessageMap != null) {
			Set<Map.Entry<String, String>> entrySet = classMessageMap.entrySet();
			for (Map.Entry<String, String> entry : entrySet) {
				try {
					errorMsgMap.put(Class.forName(entry.getKey()), entry.getValue());
				} catch (ClassNotFoundException ignored) {
				}
			}
		}

		ExceptionHandler exceptionHandler = new ExceptionHandler();
		exceptionHandler.setViewResolver(viewResolver);
		exceptionHandler.setErrorMsgMap(errorMsgMap);
		exceptionHandler.setShowExceptionName(showExceptionName);
		return exceptionHandler;
	}
}
