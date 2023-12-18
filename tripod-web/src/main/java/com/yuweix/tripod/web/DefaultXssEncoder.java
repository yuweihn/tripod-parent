package com.yuweix.tripod.web;



public class DefaultXssEncoder implements XssEncoder {
    @Override
    public String filter(String str) {
        return new HtmlFilter().filter(str);
    }
}
