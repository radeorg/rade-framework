package org.dows.rade.feign.intercept;

import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.dows.rade.feign.context.FeignContextHolder;
import org.dows.rade.feign.contract.HttpEncoding;
import org.dows.rade.feign.log.FeignLogInterceptor;
import org.dows.rade.feign.springboot.FeignSpringContextHolder;

import java.nio.charset.StandardCharsets;

/**
 * Function:
 * <p>
 * <p>
 * Date: 2022/2/10 01:04
 */
public class FeignInterceptor implements RequestInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RequestTemplate template) {
        template.header(HttpEncoding.CONTENT_TYPE, "application/json");
        String body = StrUtil.str(template.body(), StandardCharsets.UTF_8);
        FeignLogInterceptor logInterceptor = FeignSpringContextHolder.getBean(FeignLogInterceptor.class);
        String interfaceName = template.methodMetadata().targetType().getName();
        String targetMethod = template.methodMetadata().configKey();
        String target = StrUtil.format("{}.{}", interfaceName, targetMethod);
        FeignContextHolder.setLocalTime();
        logInterceptor.request(target, template.request().url(), body);
    }


}