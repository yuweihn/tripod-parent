package com.assist4j.web.freemarker;


import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;


/**
 * html标签不逃逸。
 * 比如，字符串中包含<br/>，则将该字符串折行显示，而不是将<br/>这几个字符串输出。
 * 当使用Springmvc5.×的时候使用。
 * @author yuwei
 */
public class NotEscapeHtmlOutputFormatList extends ArrayList<OutputFormat> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public NotEscapeHtmlOutputFormatList() {
		add(INSTANCE);
	}


	private static OutputFormat INSTANCE = new CommonMarkupOutputFormat<TemplateHTMLOutputModel>() {
		@Override
		public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
			out.write(textToEsc);
		}

		@Override
		public String escapePlainText(String plainTextContent) throws TemplateModelException {
			return plainTextContent;
		}

		@Override
		public boolean isLegacyBuiltInBypassed(String builtInName) throws TemplateModelException {
			return false;
		}

		@Override
		public boolean isOutputFormatMixingAllowed() {
			return true;
		}

		@Override
		protected TemplateHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) throws TemplateModelException {
			return null;
		}

		@Override
		public String getName() {
			return "HTML";
		}

		@Override
		public String getMimeType() {
			return null;
		}
	};
}

