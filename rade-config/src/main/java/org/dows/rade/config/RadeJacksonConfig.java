package org.dows.rade.config;


import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import org.dows.rade.jackson.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
@EnableConfigurationProperties(RadeJacksonProperties.class)
public class RadeJacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(RadeJacksonProperties radeJacksonProperties, ObjectProvider<Module> modules) {
        List<Module> moduleList = modules.stream().collect(Collectors.toList());
        return builder -> {
            if (Objects.equals(radeJacksonProperties.getTimestamp(), true)) {
                moduleList.add(timestampTimeModule());
            }
            if (Objects.equals(radeJacksonProperties.getBigNumberToString(), true)) {
                moduleList.add(numberToStringModule());
            }
            builder.modules(moduleList);
        };
    }

    private SimpleModule timestampTimeModule() {
        return new SimpleModule().addDeserializer(Date.class, new TimeMillisToDateDeserializer())
                .addSerializer(Date.class, new DateToTimeMillisSerializer())
                .addSerializer(LocalDateTime.class, new LocalDateTimeToTimeMillisSerializer())
                .addDeserializer(LocalDateTime.class, new TimeMillisToLocalDateTimeDeserializer());
    }

    private SimpleModule numberToStringModule() {
        return new SimpleModule()
                .addSerializer(Long.class, BigNumberSerializer.INSTANCE).addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE)
                .addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
    }
}
