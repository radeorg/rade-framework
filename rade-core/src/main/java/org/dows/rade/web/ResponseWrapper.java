package org.dows.rade.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

public class ResponseWrapper implements ResponseBodyAdvice<Object> {

    /***
     * 统一封装返回值，如果返回值是void,自动构造请求成功的返回值<br>
     * 如果返回值本身是ResponseDto，不做处理<br>
     * 其他的统一做添加请求成功的返回码。
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> arg3, ServerHttpRequest arg4, ServerHttpResponse arg5) {
        // 1.获取返回参数的全类名
        final String returnTypeName = returnType.getParameterType().getName();

        /*if (Objects.equals("void", returnTypeName)) {
            return Response.ok(null);
        }*/
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            String contextPath = request.getContextPath();
//            String contextPathExcludes = responseBodyAdviceExcludeProperties.getContextPathExclude();
//            if (hasText(contextPathExcludes) && contextPathExcludes.equals(contextPath)) {
//                return body;
//            }
        }
        if (body instanceof Response || Objects.equals(Response.class.getName(), returnTypeName)) {
            return body;
        }

        return Response.ok(body);
    }

    /**
     * 是否支持做的统一返回值处理，<br>
     * 如果返回false，将不会进入beforeBodyWrite方法
     */
    @Override
    public boolean supports(MethodParameter arg0, Class<? extends HttpMessageConverter<?>> arg1) {
        return true;
    }
}
