//package org.dows.rade.jackson;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//public class JacksonConfig {
//
//    // 定义目标格式（如：yyyy-MM-dd HH:mm:ss）
//    private static final String TARGET_PATTERN = "yyyy-MM-dd HH:mm:ss";
//
//    // 创建格式化器
//    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TARGET_PATTERN);
//
//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JavaTimeModule module = new JavaTimeModule();
//
//        // 1. 配置反序列化器：字符串→LocalDateTime（前端传递的字符串按TARGET_PATTERN解析）
//        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(FORMATTER);
//        module.addDeserializer(LocalDateTime.class, deserializer);
//
//
//        // 2. 配置序列化器：LocalDateTime→字符串（后端返回给前端的字符串按TARGET_PATTERN生成）
//        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(FORMATTER);
//        module.addSerializer(LocalDateTime.class, serializer);
//
//        // 注册模块到Jackson
//        objectMapper.registerModule(module);
//        objectMapper.setDateFormat(new SimpleDateFormat(TARGET_PATTERN));
//        //objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        SimpleModule simpleModule = new SimpleModule();
//        // 将Long类型序列化为字符串
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//
//        objectMapper.registerModule(simpleModule);
//        return objectMapper;
//    }
//}