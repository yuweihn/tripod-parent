package com.assist4j.core.exception;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assist4j.core.InterceptorUtil;
import com.assist4j.core.JsonUtil;
import com.assist4j.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;


/**
 * 通用异常处理方式
 * @author yuwei
 */
public class ExceptionHandler implements HandlerExceptionResolver {
	private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);
	private String errorPage;
	private Map<Class<?>, String> errorMsgMap;

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

	public void setErrorMsgMap(Map<Class<?>, String> errorMsgMap) {
		this.errorMsgMap = errorMsgMap;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response
			, Object handler, Exception ex) {
		log.error("", ex);
		if (InterceptorUtil.hasResponseBodyAnnotation(handler) || errorPage == null || "".equals(errorPage)) {
			return createJsonView(ex);
		}

		return createModelAndView(ex);
	}

	private ModelAndView createJsonView(Exception ex) {
		FastJsonJsonView view = new FastJsonJsonView();
		Map<String, Object> attributes = JsonUtil.objectToMap(new Response<Void>(Response.CODE_FAILURE, showMessage(ex), null));
		view.setAttributesMap(attributes);
		return new ModelAndView(view);
	}

	private ModelAndView createModelAndView(Exception ex) {
		ModelAndView mv = new ModelAndView(errorPage);
		mv.getModel().put("status", 500);
		mv.getModel().put("errorMsg", showMessage(ex));
		return mv;
	}

	private String showMessage(Exception ex) {
		Class<? extends Exception> aClz = ex.getClass();
		String msg = ex.getMessage();
		String defaultMsg = errorMsgMap == null ? null : errorMsgMap.get(aClz);

		if (msg != null && !"".equals(msg)) {
			return aClz.getName() + ": " + msg;
		}

		if (defaultMsg != null && !"".equals(defaultMsg)) {
			return aClz.getName() + ": " + defaultMsg;
		}

		return aClz.getName();
	}
}
