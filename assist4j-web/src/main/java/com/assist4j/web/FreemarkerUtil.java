package com.assist4j.web;


import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.annotation.Resource;


/**
 * @author wei
 */
public class FreemarkerUtil {
	private static Configuration cfg;
	private static String ftlPath;

	private FreemarkerUtil() {

	}


	public static String merge(String templateName, Map<String, Object> params) {
		try {
			Template temp = cfg.getTemplate(ftlPath + templateName);

			Writer out = new StringWriter();
			temp.process(params, out);
			out.flush();
			return out.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Resource
	public void setCfg(Configuration cfg) {
		FreemarkerUtil.cfg = cfg;
	}

	@Resource
	public static void setFtlPath(String ftlPath) {
		FreemarkerUtil.ftlPath = ftlPath;
	}
}
