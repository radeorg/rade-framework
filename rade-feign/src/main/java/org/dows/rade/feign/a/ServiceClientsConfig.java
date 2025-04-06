package org.dows.rade.feign.a;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class ServiceClientsConfig {

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Value("${services.order-service.url}")
    private String orderServiceUrl;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Bean
    public Map<String, String> serviceUrls() {
        Map<String, String> urls = new HashMap<>();
        urls.put("user-service", userServiceUrl);
        urls.put("order-service", orderServiceUrl);
        urls.put("product-service", productServiceUrl);
        return urls;
    }

//    @Bean
//    public UserFeignClient userFeignClient(feign.Builder feignBuilder) {
//        return feignBuilder
//               .target(UserFeignClient.class, serviceUrls().get("user-service"));
//    }
//
//    @Bean
//    public OrderFeignClient orderFeignClient(feign.Builder feignBuilder) {
//        return feignBuilder
//               .target(OrderFeignClient.class, serviceUrls().get("order-service"));
//    }
//
//    @Bean
//    public ProductFeignClient productFeignClient(feign.Builder feignBuilder) {
//        return feignBuilder
//               .target(ProductFeignClient.class, serviceUrls().get("product-service"));
//    }
}    