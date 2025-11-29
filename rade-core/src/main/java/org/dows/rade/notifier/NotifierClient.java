package org.dows.rade.notifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.exchange.ExchangeMessage;
import org.dows.rade.exchange.ExchangeRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotifierClient {

    // 通知器
    private final Map<String, Notifier> noticers;

    /**
     * 发送通知
     *
     * @param noticeMessage
     * @param returnType
     */
    public void notice(ExchangeMessage noticeMessage, Class<?> returnType) {
        ExchangeRequest request = noticeMessage.getRequest();
        List<String> handlers = request.getHandlers();
        for (String handler : handlers) {
            // todo thread and callback
            Notifier notifier = noticers.get(handler);
            if (notifier != null) {
                notifier.notice(noticeMessage, returnType);
            }
        }
    }

    /**
     * 查询通知是否成功
     *
     * @return
     */
    public Boolean isSuccess() {

        return false;
    }


    public void callback() {

    }



    /*private final RestTemplate restTemplate;

    public void notice(NoticeMessage noticeMessage) {
        // 获取 endpoint 和 HTTP 方法
        log.info("notice message: {}", noticeMessage);
        ExchangeRequest endpoint = noticeMessage.getRequest();

        HttpHeaders headers = new HttpHeaders();
//        headers.set("token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            // 针对不同 HTTP 方法采用不同的传参方式
            if (endpoint.getHttpMethod() == HttpMethod.GET) {
                // GET 请求：将参数转换为 URL 查询参数
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endpoint.getEndpoint());
                Map<String, Object> stringObjectMap = BeanUtil.beanToMap(noticeMessage);
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
                log.info("notice response: {}", body);
            } else {
                HttpEntity<?> requestEntity = new HttpEntity<>(noticeMessage, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        endpoint.getEndpoint(),
                        endpoint.getHttpMethod(),
                        requestEntity,
                        String.class
                );
                String body = response.getBody();
                log.info("notice response: {}", body);
            }
        } catch (RestClientException e) {
            log.error("Failed to rerun process instance", e);
            throw new RuntimeException("Failed to rerun process instance", e);
        }

    }*/


}
