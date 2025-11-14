package org.dows.rade.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dows.rade.util.PlaceholderUtil;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ExchangeMessage extends Serializable {
    // 存储解析后的注解元数据
    @JsonIgnore
    Map<Class<?>, RequestMetadata> metadataCache = new ConcurrentHashMap<>();

    //@JsonIgnoreProperties(ignoreUnknown = true)
    // 请求元数据类
    class RequestMetadata {
        @JsonIgnore
        HttpMethod httpMethod;
        @JsonIgnore
        String endpointTemplate;
        @JsonIgnore
        Map<String, Field> uriParamFields = new HashMap<>();
        @JsonIgnore
        Map<String, Field> pathParamFields = new HashMap<>();
        @JsonIgnore
        Map<String, Field> bodyFields = new HashMap<>();
        @JsonIgnore
        Map<String, Field> headerFields = new HashMap<>();
    }

    @JsonIgnore
    default ExchangeRequest getRequest() {
        // 获取缓存的类级别元数据
        RequestMetadata metadata = metadataCache.computeIfAbsent(
                this.getClass(),
                this::extractRequestMetadata
        );
        // 基于元数据和当前实例构建请求
        return buildRequestFromMetadata(metadata);
    }

    @JsonIgnore
    private RequestMetadata extractRequestMetadata(Class<?> clazz) {
        RequestMetadata metadata = new RequestMetadata();

        // 解析 @Uri 注解
        Uri uriAnnotation = clazz.getAnnotation(Uri.class);
        if (uriAnnotation == null) {
            throw new IllegalArgumentException("ExchangeMessage class must be annotated with @Uri");
        }

        String endpoint = uriAnnotation.value();
        if (endpoint.startsWith("get ")) {
            metadata.httpMethod = HttpMethod.GET;
            endpoint = endpoint.substring(4);
        } else if (endpoint.startsWith("post ")) {
            metadata.httpMethod = HttpMethod.POST;
            endpoint = endpoint.substring(5);
        } else if (endpoint.startsWith("put ")) {
            metadata.httpMethod = HttpMethod.PUT;
            endpoint = endpoint.substring(4);
        } else if (endpoint.startsWith("delete ")) {
            metadata.httpMethod = HttpMethod.DELETE;
            endpoint = endpoint.substring(7);
        } else {
            throw new IllegalArgumentException("httpMethod not found: " + endpoint);
        }

        metadata.endpointTemplate = endpoint;

        // 提取字段上的注解信息
        Map<Class<? extends Annotation>, Map<String, Field>> annotationFieldMap =
                AnnotationExtractor.extractFieldsByAnnotations(clazz);

        metadata.uriParamFields = annotationFieldMap.getOrDefault(UriParam.class, new HashMap<>());
        metadata.pathParamFields = annotationFieldMap.getOrDefault(PathParam.class, new HashMap<>());
        metadata.bodyFields = annotationFieldMap.getOrDefault(UriBody.class, new HashMap<>());
        metadata.headerFields = annotationFieldMap.getOrDefault(UriHeader.class, new HashMap<>());

        return metadata;
    }

    @JsonIgnore
    private ExchangeRequest buildRequestFromMetadata(RequestMetadata metadata) {
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setHttpMethod(metadata.httpMethod);

        String endpoint = metadata.endpointTemplate;

        try {
            // 处理 URI 参数
            if (!metadata.uriParamFields.isEmpty()) {
                StringBuilder uriParams = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, Field> entry : metadata.uriParamFields.entrySet()) {
                    Field field = entry.getValue();
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value != null) {
                        if (!first) {
                            uriParams.append("&");
                        }
                        uriParams.append(entry.getKey()).append("=").append(value);
                        first = false;
                    }
                }
                if (!uriParams.isEmpty()) {
                    endpoint += "?" + uriParams;
                }
            }

            // 处理路径参数
            if (!metadata.pathParamFields.isEmpty()) {
                for (Map.Entry<String, Field> entry : metadata.pathParamFields.entrySet()) {
                    Field field = entry.getValue();
                    field.setAccessible(true);
                    Object value = field.get(this);
                    String pathValue = null;
                    if (value != null) {
                        pathValue = value.toString();
                    } else {
                        //@PathParam(value = "host", defValue = "${hina.eaglee.dolphin.host:http://10.0.20.25:12345}")
                        String defValue = field.getAnnotation(PathParam.class).defValue();
                        if (!defValue.isEmpty()) {
                            pathValue = PlaceholderUtil.resolve(defValue);
                        }
                    }
                    if (pathValue != null) {
                        endpoint = endpoint.replace("{" + entry.getKey() + "}", pathValue);
                    }
                }
            }

            exchangeRequest.setEndpoint(endpoint);

            // 处理 Body 参数
            if (!metadata.bodyFields.isEmpty()) {
                for (Map.Entry<String, Field> entry : metadata.bodyFields.entrySet()) {
                    Field field = entry.getValue();
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value != null) {
                        exchangeRequest.addBody(entry.getKey(), value);
                    } else {
                        //@PathParam(value = "host", defValue = "${hina.eaglee.dolphin.host:http://10.0.20.25:12345}")
                        String defValue = field.getAnnotation(UriBody.class).defValue();
                        if (!defValue.isEmpty()) {
                            value = PlaceholderUtil.resolve(defValue);
                            exchangeRequest.addBody(entry.getKey(), value);
                        }
                    }
                }
            }

            // 处理 Header 参数
            if (!metadata.headerFields.isEmpty()) {
                for (Map.Entry<String, Field> entry : metadata.headerFields.entrySet()) {
                    Field field = entry.getValue();
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value != null) {
                        exchangeRequest.addHeader(entry.getKey(), value);
                    } else {
                        //@PathParam(value = "host", defValue = "${hina.eaglee.dolphin.host:http://10.0.20.25:12345}")
                        String defValue = field.getAnnotation(UriHeader.class).defValue();
                        if (!defValue.isEmpty()) {
                            value = PlaceholderUtil.resolve(defValue);
                            exchangeRequest.addHeader(entry.getKey(), value);
                        }
                    }
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field values", e);
        }

        return exchangeRequest;
    }
    /*  @JsonIgnore
    default ExchangeRequest getRequest() {
        Uri annotation = this.getClass().getAnnotation(Uri.class);
        if (annotation == null) {
            throw new IllegalArgumentException("NoticeRequest class must be annotated with @Uri");
        }
        String endpoint = annotation.value();
        HttpMethod httpMethod = null;
        if (endpoint.startsWith("get ")) {
            httpMethod = HttpMethod.GET;
            endpoint = endpoint.substring(4);
        } else if (endpoint.startsWith("post ")) {
            httpMethod = HttpMethod.POST;
            endpoint = endpoint.substring(5);
        } else if (endpoint.startsWith("put ")) {
            httpMethod = HttpMethod.PUT;
            endpoint = endpoint.substring(4);
        } else if (endpoint.startsWith("delete ")) {
            httpMethod = HttpMethod.DELETE;
            endpoint = endpoint.substring(7);
        }
        if (httpMethod == null) {
            throw new IllegalArgumentException("httpMethod not found: " + endpoint);
        }
        // 提取url参数
        //Map<String, String> urlParams = UrlParamExtractor.extractParameters(endpoint);

        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setHttpMethod(httpMethod);
        exchangeRequest.setEndpoint(endpoint);
        //Map<String, Object> stringObjectMap = BeanUtil.beanToMap(this);
        Map<Class<? extends Annotation>, Map<String, Object>> classMapMap = AnnotationExtractor.extractFiledValueByAnnotations(this);
        // 处理url追加参数

        Map<String, Object> paramMap = classMapMap.get(UriParam.class);
        if (paramMap != null) {
            StringBuilder uriParams = new StringBuilder();
            paramMap.forEach((k, v) -> {
                uriParams.append(k).append("=").append(v).append("&");
            });
            String uriParamStr = uriParams.deleteCharAt(uriParams.length() - 1).toString();
            endpoint += "?" + uriParamStr;
            exchangeRequest.setEndpoint(endpoint);
        }

        // todo 统一处理uri 中的path参数
        Map<String, Object> pathMap = classMapMap.get(PathParam.class);
        if (pathMap != null) {
            *//*for (String k : pathMap.keySet()){
                endpoint = endpoint.replace("{" + k + "}", pathMap.get(k).toString());
            }*//*
            for (Map.Entry<String, Object> entry : pathMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                endpoint = endpoint.replace("{" + key + "}", value);
            }
            exchangeRequest.setEndpoint(endpoint);
        }

    //Map<String, Object> body = BeanUtil.beanToMap(this);
        // 获取body参数对象
    Map<String, Object> bodyMap = classMapMap.get(UriBody.class);
    if (bodyMap != null) {
        // 填充body参数对象
        bodyMap.forEach(exchangeRequest::addBody);
    }

        // 填充header  header.forEach(endpointRequest::addHeader);
        Map<String, Object> headerMap = classMapMap.get(UriHeader.class);
        if (headerMap != null) {
            headerMap.forEach(exchangeRequest::addHeader);
        }

    return exchangeRequest;
    }*/

    default <T extends ExchangeMessage> T toRequestEntity(Class<T> noticeRequestClass) {
        return noticeRequestClass.cast(this);
    }
}
