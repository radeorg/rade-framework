package org.dows.rade.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.context.AppContext;
import org.dows.rade.exception.RadeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置高优先级
public class AppContextSetupFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest httpServletRequest) {
                // 从请求头或参数获取appId
                String appId = httpServletRequest.getHeader("AppId");
                if (appId == null || appId.isBlank()) {
                    throw new RadeException("appId不能为空");
                }
                // 设置appId到ThreadLocal
                AppContext.setAppId(appId);
                chain.doFilter(request, response);
            }
        } finally {
            // 不要在这里清除，可能还有其他过滤器需要使用
        }
    }
}