package org.dows.rade.feign.springboot;

import feign.Client;
import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.dows.rade.feign.decoder.FeignErrorDecoder;
import org.dows.rade.feign.log.DefaultLogInterceptor;
import org.dows.rade.feign.log.FeignLogInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Function:
 * <p>
 * <p>
 * Date: 2020/7/25 01:16
 */
@Configuration
@EnableConfigurationProperties(FeignPlusConfigurationProperties.class)
public class FeignPlusAutoConfiguration {

    private FeignPlusConfigurationProperties feignPlusConfigurationProperties;

    public FeignPlusAutoConfiguration(FeignPlusConfigurationProperties feignPlusConfigurationProperties) {
        this.feignPlusConfigurationProperties = feignPlusConfigurationProperties;
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(feignPlusConfigurationProperties.getMaxIdleConnections(),
                feignPlusConfigurationProperties.getKeepAliveDuration(), TimeUnit.MINUTES);
    }


    @Bean(value = "client")
    public Client okHttpClient(ConnectionPool connectionPool) {
        OkHttpClient delegate = new OkHttpClient().newBuilder()
                // skip ssl
                .hostnameVerifier((hostname, session) -> true)
                .connectionPool(connectionPool)
                .connectTimeout(feignPlusConfigurationProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(feignPlusConfigurationProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(feignPlusConfigurationProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .build();
        return new feign.okhttp.OkHttpClient(delegate);
    }


    @Bean
    @ConditionalOnMissingBean(FeignSpringContextHolder.class)
    public FeignSpringContextHolder feignSpringContextHolder() {
        return new FeignSpringContextHolder();
    }

    @Bean()
    @ConditionalOnMissingBean(FeignLogInterceptor.class)
    public FeignLogInterceptor feignLogInterceptor() {
        return new DefaultLogInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(MeterRegistryCustomizer.class)
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(
            @Value("${spring.application.name}") String appName) {
        return registry -> registry.config().commonTags("app", appName);
    }

    @Bean
    @ConditionalOnMissingBean(FeignErrorDecoder.class)
    public FeignErrorDecoder feignErrorDecoder() {
        return (methodKey, response, e) -> e;
    }
}
