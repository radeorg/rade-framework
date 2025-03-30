package org.dows.rade.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;


@Configuration
public class JacksonConfig {

    @Value("${rade.jackson.timestamp}")
    private boolean timestamp;

    @Bean
    @ConditionalOnProperty(name = "rade.jackson.timestamp", havingValue = "true")
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        if (timestamp) {
            objectMapper.registerModule(setupTimestampTimeProcessor());
        }
        return objectMapper;
    }

    public SimpleModule setupTimestampTimeProcessor() {
        return new SimpleModule().addDeserializer(Date.class, new TimeMillisToDateDeserializer())
                .addSerializer(Date.class, new DateToTimeMillisSerializer());
    }
}
