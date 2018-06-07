package com.assist4j.web;


import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;
import javax.annotation.Resource;


/**
 * @author wei
 */
public class TextUtil {
	private static ResourceBundleMessageSource messageSource;

	private TextUtil() {

	}

	public static String getText(String key) {
		return getText(key, LocaleUtil.getLocale());
	}


	public static String getText(String key, Locale locale) {
		return messageSource.getMessage(key, new Object[] {}, locale);
	}

	public static String getText(String key, String locale) {
		return messageSource.getMessage(key, new Object[] {}, LocaleUtil.getLocaleFromString(locale));
	}

	public static String getText(String key, Object[] params) {
		return messageSource.getMessage(key, params, LocaleUtil.getLocale());
	}

	public static String getText(String key, Object[] params, String locale) {
		return messageSource.getMessage(key, params, LocaleUtil.getLocaleFromString(locale));
	}

	//////////////////////////////////////////////////////////////////////////////////////
	@Resource
	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		TextUtil.messageSource = messageSource;
	}
}
