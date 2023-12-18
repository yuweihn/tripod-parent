package com.yuweix.tripod.web;



/**
 * XSS漏洞过滤
 */
public interface XssEncoder {
    String filter(String str);
}
