package com.assist4j.web.freemarker;


import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * @author wei
 */
public class FreemarkerUtil {
	private static Configuration cfg;
	private static String ftlPath = "";

	private FreemarkerUtil() {

	}


	public static String merge(String template) {
		return merge(template, null);
	}
	public static String merge(String template, Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setCfg(Configuration cfg) {
		FreemarkerUtil.cfg = cfg;
	}

	public void setFtlPath(String ftlPath) {
		FreemarkerUtil.ftlPath = ftlPath;
	}
}
