package org.dows.rade.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dows.rade.model.UriSignature;
import org.dows.rade.status.CommonStatusCode;
import org.dows.rade.status.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回信息
 */
//@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
@Schema(name = "响应", title = "响应数据")
public class Response<T> implements Serializable {
    //@ApiModelProperty(value = "状态码")
    @Schema(title = "状态码")
    private String code;
    //@ApiModelProperty(value = "描述")
    @Schema(title = "描述")
    private String description;
    //@ApiModelProperty("状态(成功:true,失败:false)")
    @Schema(title = "状态(成功:true,失败:false)")
    private Boolean status = true;

    //@ApiModelProperty(value = "响应时间")
    @Schema(title = "响应时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timestamp = new Date();
    //@ApiModelProperty(value = "响应数据")
    @Schema(title = "响应数据")
    private T data;

    //@JsonIgnore
    //private HashMap<String, Object> ext = new HashMap<>();

    public Response() {
    }

    public Response(int code, String description) {
        this.code = String.valueOf(code);
        this.description = description;
    }

    public Response(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public Response(String code, String description, T data) {
        this.code = code;
        this.description = description;
        this.data = data;
    }


    public Response(Boolean status, String code, String description, T data) {
        this.code = code;
        this.description = description;
        this.data = data;
        this.status = status;
    }

    public Response(Boolean status, StatusCode statusCode) {
        this.status = status;
        this.code = statusCode.getCode();
        this.description = statusCode.getDescribe();
    }

    public Response(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.description = statusCode.getDescribe();
    }

    public Response(StatusCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.description = statusCode.getDescribe();
        this.data = data;

    }

    public  Response<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Response<T> setCode(String code) {
        this.code = code;
        return this;
    }
    public Response<T> setDescription(String description) {
        this.description = description;
        return this;
    }



    public static <T> Response<T> ok() {
        Response<T> api = new Response(CommonStatusCode.SUCCESS);
        api.setStatus(true);
        return api;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Response<T> ok(T data) {
        Response<T> api = new Response(CommonStatusCode.SUCCESS);
        api.setStatus(true);
        api.setData(data);
        return api;
    }

    public static <T> Response<T> ok(StatusCode statusCode) {

        return new Response<T>(statusCode.getCode(), statusCode.getDescribe(), null);
    }
    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Response<T> ok(T data, String message) {

        return new Response<T>(CommonStatusCode.SUCCESS.getCode(), message, data);
    }




    public static void main(String[] args) throws JsonProcessingException {
        Response response = Response.failed(4000, "adasdds");
        Map<String, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        map.put("aaa", 11212);
        //response.putAll(map);
        UriSignature uriSignature = new UriSignature();
        uriSignature.setUri("weqeqwe");
        uriSignature.setDescription("descr");
        response.setData(uriSignature);
        System.out.println(objectMapper.writeValueAsString(response));
    }

    public static <T> Response<T> failed(int code, String msg) {
        return new Response(code, msg);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public static <T> Response<T> failed(String code, String message) {
        return new Response<T>(false, code, message, null);
    }
    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Response<T> failed(String message) {
        return new Response<T>(false, CommonStatusCode.FAILED.getCode(), message, null);
    }
    /**
     * 失败返回结果
     *
     * @param statusCode 错误码
     */
    public static <T> Response<T> failed(StatusCode statusCode) {

        return new Response<T>(false, statusCode);
    }


    /**
     * 统一返回
     */
    public ResponseEntity<Response<T>> responseEntity() {
        return new ResponseEntity<Response<T>>(this, HttpStatus.valueOf(this.getCode()));
    }

//    /**
//     * 未登录返回结果
//     * 未授权返回结果
//     * 其他 参考 AuthStatusCode
//     *
//     * @param authStatusCode
//     * @param <T>
//     * @return
//     */
//    public static <T> Response<T> authFailed(AuthStatusCode authStatusCode) {
//        Response<T> api = new Response(authStatusCode);
//        api.setStatus(false);
//        return api;
//    }
//
//    /**
//     * 未登录返回结果
//     */
//    public static <T> Response<T> unauthorized(T data) {
//        return new Response<T>(AuthStatusCode.UNAUTHORIZED.getCode(), AuthStatusCode.UNAUTHORIZED.getDescribe(), data);
//    }
//
//    /**
//     * 未授权返回结果
//     */
//    public static <T> Response<T> forbidden(T data) {
//        return new Response<T>(AuthStatusCode.FORBIDDEN.getCode(), AuthStatusCode.FORBIDDEN.getDescribe(), data);
//    }


}