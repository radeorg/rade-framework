package org.dows.rade.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.dows.rade.config.RadeJacksonProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
@EnableConfigurationProperties(RadeJacksonProperties.class)
public class RadeJacksonConfig {


    // 定义目标格式（如：yyyy-MM-dd HH:mm:ss）
    private static final String TARGET_PATTERN = "yyyy-MM-dd HH:mm:ss";

    // 创建格式化器
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TARGET_PATTERN);

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();

        // 1. 配置反序列化器：字符串→LocalDateTime（前端传递的字符串按TARGET_PATTERN解析）
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(FORMATTER);
        module.addDeserializer(LocalDateTime.class, deserializer);


        // 2. 配置序列化器：LocalDateTime→字符串（后端返回给前端的字符串按TARGET_PATTERN生成）
        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(FORMATTER);
        module.addSerializer(LocalDateTime.class, serializer);

        // 注册模块到Jackson
        objectMapper.registerModule(module);
        objectMapper.setDateFormat(new SimpleDateFormat(TARGET_PATTERN));
        //objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule simpleModule = new SimpleModule();
        // 将Long类型序列化为字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

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
