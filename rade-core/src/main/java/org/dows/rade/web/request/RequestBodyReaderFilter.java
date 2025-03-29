package org.dows.rade.web.request;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class RequestBodyReaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            String contentType = request.getContentType();
            if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
                chain.doFilter(request, response);
            } else {
                ServletRequest requestWrapper = null;
                if (request instanceof HttpServletRequest) {
                    requestWrapper = new RequestWrapper((HttpServletRequest) request);
                }
                chain.doFilter(Objects.requireNonNullElse(requestWrapper, request), response);
            }
        } catch (IOException | ServletException e) {
            log.error("", e);
        }
    }

    @Override
    public void destroy() {

    }
}