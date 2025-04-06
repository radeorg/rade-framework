package org.dows.rade.feign.decoder;

import cn.hutool.core.util.StrUtil;
import feign.Response;
import feign.gson.GsonDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.feign.context.FeignContextHolder;
import org.dows.rade.feign.log.FeignLogInterceptor;
import org.dows.rade.feign.metrics.Metrics;
import org.dows.rade.feign.springboot.FeignSpringContextHolder;

import java.lang.reflect.Type;
import java.time.Duration;


/**
 * Function:
 * <p>
 * <p>
 * Date: 2022/2/7 23:46
 */
@Slf4j
public class FeignPlusDecoder extends GsonDecoder {
    @SneakyThrows
    @Override
    public Object decode(Response response, Type type) {
        Object decode = super.decode(response, type);
        // build target info.
        String interfaceName = response.request().requestTemplate().methodMetadata().targetType().getName();
        String targetMethod = response.request().requestTemplate().methodMetadata().configKey();
        String target = StrUtil.format("{}.{}", interfaceName, targetMethod);
        FeignLogInterceptor logInterceptor = FeignSpringContextHolder.getBean(FeignLogInterceptor.class);
        logInterceptor.response(target, response.request().url(), decode);
        Long start = FeignContextHolder.getLocalTime();
        long end = System.currentTimeMillis();
        Metrics.time(Duration.ofMillis(end - start), "feign_call", "target", target, "status", "success");
        return decode;
    }
}
