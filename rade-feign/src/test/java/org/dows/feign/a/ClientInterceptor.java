package org.dows.feign.a;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.Resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;


public class ClientInterceptor implements RequestInterceptor {
    @Resource
    private AppConfig appConfig;
    @Resource
    private XunProperties xunProperties;

    @Override
    public void apply(RequestTemplate template) {
        if (template.url().startsWith(xunProperties.getContextPath())) {
            String appId = appConfig.getId(); // 从配置或服务中获取
            String appKey = appConfig.getKey(); // 从配置或服务中获取
            byte[] body = template.body(); // 需要从template或其他地方获取请求体内容
            Boolean signUpperCase = true; // 根据需要设置

            String sign = xunSign(appId, appKey, Arrays.toString(body), signUpperCase);
            template.header("Content-Type", "application/json");
            template.header("Authorization", "Bearer " + sign);
            template.header("data", Base64.getEncoder().encodeToString(body));

            process(template, StandardCharsets.UTF_8, sign, body);
        }
    }

    private void process(RequestTemplate template, Charset charset, String key, byte[] data) {
        template.removeHeader("Content-Type");
        template.header("Content-Type", "application/json");
        template.body(data, charset);
    }

    private String encode(String string, Charset charset) {
        try {
            return URLEncoder.encode(string, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode URL", e);
        }
    }

    private String xunSign(String appId, String appKey, String bodyStr, Boolean signUpperCase) {
        // 生成签名逻辑
        String sign = appId + "-" + appKey + "-" + bodyStr; // 示例
        return signUpperCase ? sign.toUpperCase() : sign;
    }
}
