package org.dows.rade.feign.contract.annotation;

import org.dows.rade.feign.contract.AnnotatedParameterProcessor;
import org.dows.rade.feign.contract.HttpEncoding;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Function:
 * <p>
 * Date: 2022/2/10 00:21
 */
public class RequestBodyProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestBody> ANNOTATION = RequestBody.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation, Method method) {
        context.getMethodMetadata().template().header(HttpEncoding.CONTENT_TYPE, "application/json");
        return true;
    }

}