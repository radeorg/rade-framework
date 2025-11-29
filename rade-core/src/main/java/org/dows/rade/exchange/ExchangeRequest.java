package org.dows.rade.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRequest implements Serializable {
    @Getter
    @Setter
    @JsonIgnore
    private HttpMethod httpMethod;
    @Getter
    @Setter
    private String endpoint;
    @Getter
    private final Map<String, Object> headers = new HashMap<>();
    @Getter
    private final Map<String, Object> body = new HashMap<>();
    //处理器名称
    @Getter
    private final List<String> handlers = new ArrayList<>();


    //private Retriable retriable;
    //private List<Interceptor> interceptors;

    public ExchangeRequest addHeader(String key, Object value) {
        headers.put(key, value);
        return this;
    }

    public ExchangeRequest addBody(String key, Object value) {
        body.put(key, value);
        return this;
    }


    public ExchangeRequest addHandler(String handler) {
        handlers.add(handler);
        return this;
    }
}
