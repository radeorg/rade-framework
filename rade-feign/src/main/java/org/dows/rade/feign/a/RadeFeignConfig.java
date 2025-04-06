package org.dows.rade.feign.a;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.Decoder;
import feign.codec.StringDecoder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 不要在这个配置类上并@Configuration注解，
 * 因为这不是注册到应用的ApplicationContext，而是注册到OpenFeign为Client创建的ApplicationContext。
 *
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 10/16/2024 9:13 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Slf4j
@Configuration
//@AutoConfiguration(before = {FeignAutoConfiguration.class})
public class RadeFeignConfig /*implements RequestInterceptor*/ {
    /**
     * token请求头名称
     */
    public static final String AUTHORIZATION = "Authorization";
    /**
     * 不需要token请求头标识
     */
    public static final String NO_NEED_TO_TOKEN = "No-Need-To-Token";

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("初始化RemoteFeignConfig");
    }


    /**
     * 默认的超时配置
     * 当default-to-properties配置为false时，
     * 这个Request.Options就会覆盖application.yaml中添加的配置。所以要将default-to-properties配置为true，配置才生效。
     *
     * @return
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(5, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, false);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new DefaultRetry();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return this::apply;
    }

    /*@Bean
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        //return new SpringEncoder(feignHttpMessageConverter());
        //return new SpringEncoder(messageConverters);
    }*/

    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        //return new SpringDecoder(feignHttpMessageConverter());
        //return new SpringDecoder(messageConverters);
        return new StringDecoder();
    }

    /*@Bean
    public Decoder feignDecoder() {
        TxtPlainMessageConverter txtPlainMessageConverter = new TxtPlainMessageConverter();
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(txtPlainMessageConverter);
        return new SpringDecoder(objectFactory);
    }*/

    /**
     * 设置解码器为fastjson
     *
     * @return
     */
    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters();
        httpMessageConverters.getConverters().add(this.getJsonConverter());
        httpMessageConverters.getConverters().add(new StringHttpMessageConverter());
        return () -> httpMessageConverters;
    }

    private MappingJackson2HttpMessageConverter getJsonConverter() {
        //FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE);
        MediaType testPlain = MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE);
        supportedMediaTypes.add(mediaTypeJson);
        supportedMediaTypes.add(testPlain);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setObjectMapper(objectMapper);
        /*FastJsonConfig config = new FastJsonConfig();
        config.getSerializeConfig().put(JSON.class, new SwaggerJsonSerializer());
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);*/

        return converter;
    }

    /**
     * 拦截
     *
     * @param requestTemplate
     */
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            //user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36
            requestTemplate.header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
        }
        //获取接口是否不需要加载token
        boolean noNeedToToken = requestTemplate.headers().containsKey(NO_NEED_TO_TOKEN);
        //取反就是需要加载token
        if (!noNeedToToken) {
            //获取用户认证token
            String token = this.getToken();
            //把用户认证token添加到请求头中
            requestTemplate.header(AUTHORIZATION, token);
        }
    }

    /**
     * 使用系统配置默认用户获取token
     *
     * @return token 用户登录凭证
     */
    public String getToken() {
        //开始构建请求参数
        Map<String, Object> params = new HashMap<>(4);
        //设置用户名
        params.put("loginName", "");
        //设置密码
        params.put("password", "");
        //使用系统配置的用户名密码进行登录
        // todo 获取token
        return null;
    }


}