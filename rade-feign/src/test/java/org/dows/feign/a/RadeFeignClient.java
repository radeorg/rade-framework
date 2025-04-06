package org.dows.feign.a;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 10/16/2024 9:13 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
//@FeignClient(name = "vacFeignClient", url = "http://127.0.0.1", configuration = RemoteFeignConfig.class/*,fallback = FeignClientFallback.class*/)
public interface RadeFeignClient {

    /**
     * GET 方式
     *
     * @param uri     uri
     * @param headers 请求头
     * @param object  请求参数
     * @return 返回值[string]
     */
    @GetMapping
    Object get(URI uri, @RequestHeader Map<String, Object> headers, Object object);

    /**
     * POST 方式
     *
     * @param uri     uri
     * @param headers 请求头
     * @param object  请求参数
     * @return 返回值[string]
     */
    @PostMapping
    Object post(URI uri, @RequestHeader Map<String, Object> headers, Object object);


    /**
     * PUT 方式
     *
     * @param uri     uri
     * @param headers 请求头
     * @param object  请求参数
     * @return 返回值[string]
     */
    @PutMapping
    Object put(URI uri, @RequestHeader Map<String, Object> headers, Object object);


    /**
     * DELETE 方式
     *
     * @param uri     uri
     * @param headers 请求头
     * @param object  请求参数
     * @return 返回值[string]
     */
    @DeleteMapping
    Object delete(URI uri, @RequestHeader Map<String, Object> headers, Object object);


}