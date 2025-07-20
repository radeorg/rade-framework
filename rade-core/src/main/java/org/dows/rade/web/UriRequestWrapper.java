package org.dows.rade.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class UriRequestWrapper extends HttpServletRequestWrapper {
    private final String newRequestURI;

    public UriRequestWrapper(HttpServletRequest request, String newRequestURI) {
        super(request);
        this.newRequestURI = newRequestURI;
    }

    @Override
    public String getRequestURI() {
        return newRequestURI;
    }

    // 如果需要，可以重写其他方法，比如 getServletPath(), getContextPath() 等
}
