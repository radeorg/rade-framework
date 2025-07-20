package org.dows.rade.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.aac.AacContext;
import org.dows.rade.context.AppContext;
import org.dows.rade.exception.RadeException;
import org.dows.rade.web.UriRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置高优先级
public class AppContextSetupFilter implements Filter {
    // 改为Pattern数组用于高效匹配
    private Map<String, Pattern[]> WHITELIST_PATTERN_MAP = new ConcurrentHashMap<>();
    //    private String[] whitelist;
    private static final Pattern USER_SPACE_PATH_PATTERN = Pattern.compile("^/([^/]+)(.*)$");
    private AacContext aacContext;

    /*@Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludedUrlsParam = filterConfig.getInitParameter("whitelist");
        if (excludedUrlsParam != null) {
            whitelist = excludedUrlsParam.split(",");
        }
    }*/


    /**
     * 接收字符串数组并转换为Pattern数组
     */
    public void syncWhitelistByAppId(String appId) {
        String[] whitelist = aacContext.getWhitelist(appId);
        if (whitelist != null) {
            Pattern[] whitelistPatterns = new Pattern[whitelist.length];
            for (int i = 0; i < whitelist.length; i++) {
                whitelistPatterns[i] = Pattern.compile(whitelist[i]);
            }
            WHITELIST_PATTERN_MAP.put(appId, whitelistPatterns);
        }
    }

    private String modifyURI(String originalURI) {
        String substring = originalURI.substring(1);
        originalURI = substring.substring(0,substring.indexOf("/"));
        return originalURI;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest httpServletRequest) {
                //
                String requestURI = httpServletRequest.getRequestURI();
                Matcher matcher = USER_SPACE_PATH_PATTERN.matcher(requestURI);
                String appId;
                if (matcher.matches()) {
                    String namespace = matcher.group(1);
                    String newURI = modifyURI(requestURI);
                    UriRequestWrapper uriWrapperRequest = new UriRequestWrapper(httpServletRequest,newURI);
                    appId = aacContext.getAppIdByNamespace(namespace);
                    // 设置appId到ThreadLocal
                    AppContext.setAppId(appId);
                    // 白名单匹配逻辑：使用正则表达式进行匹配
                    Pattern[] whitelistPatterns = WHITELIST_PATTERN_MAP.get(appId);
                    if (whitelistPatterns != null) {
                        // http://dev.bole.com/dd/cc/fff -> /cc/fff
                        String path = requestURI.substring(namespace.length() + 1);
                        for (Pattern pattern : whitelistPatterns) {
                            // 剔除组织空间路径
                            if (pattern.matcher(path).matches()) {
                                chain.doFilter(uriWrapperRequest, response);
                                return;
                            }
                        }
                    }
                } else {
                    // 从请求头或参数获取appId
                    appId = httpServletRequest.getHeader("AppId");
                    // 设置appId到ThreadLocal
                    AppContext.setAppId(appId);
                    String[] whitelist = aacContext.getWhitelist(appId);
                    if (whitelist != null) {
                        //
                        for (String item : whitelist) {
                            if (requestURI.startsWith(item)) {
                                chain.doFilter(request, response);
                                return;
                            }
                        }
                    }
                }
                if (appId == null || appId.isBlank()) {
                    throw new RadeException("appId不能为空");
                }
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 不要在这里清除，可能还有其他过滤器需要使用
        }
    }
}