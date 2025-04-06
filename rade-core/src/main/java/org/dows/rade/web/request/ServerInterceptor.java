//package org.dows.rade.web.request;
//
//import jakarta.annotation.Resource;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//public class ServerInterceptor extends OncePerRequestFilter {
//    @Resource
//    private AppConfig appConfig;
//    @Resource
//    private XunProperties xunProperties;
//
//    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // 认证放行与协议验证鉴权逻辑
//        String authorization = request.getHeader("Authorization");
//        String requestURI = request.getRequestURI();
//        if (requestURI.startsWith(xunProperties.getContextPath()) && authorization != null && authorization.startsWith("Bearer ")) {
//            String appId = appConfig.getId(); // 从配置或服务中获取
//            String appKey = appConfig.getKey(); // 从配置或服务中获取
//            Boolean signUpperCase = true; // 根据需要设置
//            String generatedSign = xunSign(appId, appKey, getRequestBody(request), signUpperCase);
//            String token = authorization.substring(7);
//            if (generatedSign.equals(token)) {
//                filterChain.doFilter(request, response);
//            } else {
//                // 验证不通过
//                throw new RuntimeException("验证不通过");
//            }
//        }
//        // 放行
//        filterChain.doFilter(request, response);
//    }
//
//    private String xunSign(String appId, String appKey, String bodyStr, Boolean signUpperCase) {
//        try {
//            String data = appId + (bodyStr != null ? bodyStr : "");
//            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
//            SecretKeySpec secretKey = new SecretKeySpec(appKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
//            mac.init(secretKey);
//            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            String signature = Base64.getEncoder().encodeToString(hash);
//            return signUpperCase ? signature.toUpperCase() : signature;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate signature", e);
//        }
//    }
//
//    private String getRequestBody(HttpServletRequest request) throws IOException {
//        StringBuilder requestBody = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                requestBody.append(line);
//            }
//        }
//        return requestBody.toString();
//    }
//}