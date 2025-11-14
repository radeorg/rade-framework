package org.dows.rade.web.filter;//package com.hina.cloud.eaglee.config;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//@WebFilter("/*")
//public class HeaderFilter implements Filter {
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String requestURI = httpRequest.getRequestURI();
//
//        // 关键：放行 OpenAPI 和 Knife4j 所有路径
//        if (requestURI.equals("/swagger-config")
//                || requestURI.startsWith("/v3/api-docs/")
//                || requestURI.equals("/doc.html")
//                || requestURI.startsWith("/webjars/")) {
//            chain.doFilter(request, response); // 直接放行，不做拦截处理
//            return;
//        }
//
//        // 你的其他过滤逻辑（如添加头信息）
//        // ...
//
//        chain.doFilter(request, response);
//    }
//}