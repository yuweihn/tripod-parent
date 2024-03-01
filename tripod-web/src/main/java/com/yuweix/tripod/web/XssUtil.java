package com.yuweix.tripod.web;



/**
 * Xss过滤器，用于去除XSS漏洞隐患。
 * @author yuwei
 */
public abstract class XssUtil {
    public static String filter(String val) {
        val = val.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        val = val.replaceAll("%3C", "&lt;").replaceAll("%3E", "&gt;");
        val = val.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        val = val.replaceAll("%28", "&#40;").replaceAll("%29", "&#41;");
        val = val.replaceAll("'", "&#39;");
        val = val.replaceAll("eval\\((.*)\\)", "");
        val = val.replaceAll("[\\\"\\'][\\s]*javascript:(.*)[\\\"\\']", "\"\"");
        val = val.replaceAll("script", "");
        return val;
    }
}
