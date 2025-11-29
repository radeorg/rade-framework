package org.dows.rade.exchange;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeClient {
    // todo 后期调整为webclient
    //private final WebClient webClient;
    private final RestTemplate restTemplate;

    private static final Set<Class<?>> BASIC_TYPES = Set.of(
            String.class, Boolean.class, Character.class,
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class
    );

    private boolean isBasicType(Class<?> clazz) {
        return clazz.isPrimitive() || BASIC_TYPES.contains(clazz);
    }

    public <T> T exchange(ExchangeMessage message, Class<T> responseType) {
        log.info("exchange request : {}, response: {}", message, responseType);
        ExchangeRequest exchangeRequest = message.getRequest();

        HttpHeaders headers = new HttpHeaders();
        exchangeRequest.getHeaders().forEach((key, value) -> headers.add(key, value.toString()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            // 针对不同 HTTP 方法采用不同的传参方式
            if (exchangeRequest.getHttpMethod() == HttpMethod.GET) {
                // GET 请求：将参数转换为 URL 查询参数
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(exchangeRequest.getEndpoint());
                Map<String, Object> stringObjectMap = BeanUtil.beanToMap(message);
                // 添加查询参数
                stringObjectMap.forEach(builder::queryParam);
                // 这里需要将对象的属性转换为查询参数
                HttpEntity<?> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<T> response = restTemplate.exchange(
                        builder.toUriString(),
                        exchangeRequest.getHttpMethod(),
                        requestEntity,
                        responseType
                );
                //String body = response.getBody();
                if(response.getStatusCode().is2xxSuccessful()){
                    T t = response.getBody();
                    log.info("exchange get response: {}", t);
                    return t;
                } else {
                    throw new RestClientException("调用报错：");
                }
                //return JSONUtil.toBean(body, responseType);
            } else {
                HttpEntity<?> requestEntity = new HttpEntity<>(message, headers);
                ResponseEntity<T> response = restTemplate.exchange(
                        exchangeRequest.getEndpoint(),
                        exchangeRequest.getHttpMethod(),
                        requestEntity,
                        responseType
                );
                if(response.getStatusCode().is2xxSuccessful()){
                    T t = response.getBody();
                    log.info("exchange post|put|delete response: {}", t);
                    return t;
                } else {
                    throw new RestClientException("调用报错：");
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to rerun process instance", e);
            throw new RuntimeException("Failed to rerun process instance", e);
        }
    }


    /**
     * 流式调用
     * @param message
     * @param responseType
     * @return
     * @param <T>
     */
    public <T> T streamExchange(ExchangeMessage message, Class<T> responseType) {
        log.info("exchange message: {}", message);
        ExchangeRequest endpoint = message.getRequest();

        HttpHeaders headers = new HttpHeaders();
//        headers.set("token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            // 针对不同 HTTP 方法采用不同的传参方式
            if (endpoint.getHttpMethod() == HttpMethod.GET) {
                // GET 请求：将参数转换为 URL 查询参数
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endpoint.getEndpoint());
                Map<String, Object> stringObjectMap = BeanUtil.beanToMap(message);
                // 添加查询参数
                stringObjectMap.forEach(builder::queryParam);
                // 这里需要将 dolphinRequest 对象的属性转换为查询参数
                HttpEntity<?> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        endpoint.getHttpMethod(),
                        requestEntity,
                        String.class
                );
                String body = response.getBody();
                log.info("notifier response: {}", body);
                return BeanUtil.toBean(body, responseType);
            } else {
                HttpEntity<?> requestEntity = new HttpEntity<>(message, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        endpoint.getEndpoint(),
                        endpoint.getHttpMethod(),
                        requestEntity,
                        String.class
                );
                String body = response.getBody();
                log.info("notifier response: {}", body);
                return BeanUtil.toBean(body, responseType);
            }
        } catch (RestClientException e) {
            log.error("Failed to rerun process instance", e);
            throw new RuntimeException("Failed to rerun process instance", e);
        }
    }

}
