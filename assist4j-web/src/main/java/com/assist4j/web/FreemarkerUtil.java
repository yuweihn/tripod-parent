package com.assist4j.web;


import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author wei
 */
public class FreemarkerUtil {
	private static Configuration cfg;
	private static String ftlPath = "";
	private static boolean exposeSpringMacroHelpers = false;

	private FreemarkerUtil() {

	}


	public static String merge(String template) {
		return merge(template, null);
	}
	public static String merge(String template, Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		if (exposeSpringMacroHelpers) {
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			params.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, new RequestContext(request
					, response, request.getServletContext(), params));
		}

		try {
			Template temp = cfg.getTemplate(ftlPath + template);

			Writer out = new StringWriter();
			temp.process(params, out);
			out.flush();
			return out.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static HttpServletRequest getRequest() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return sra == null ? null : sra.getRequest();
	}

	private static HttpServletResponse getResponse() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return sra == null ? null : sra.getResponse();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setCfg(Configuration cfg) {
		FreemarkerUtil.cfg = cfg;
	}

	public void setFtlPath(String ftlPath) {
		FreemarkerUtil.ftlPath = ftlPath;
	}

	public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
		FreemarkerUtil.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
	}
}
