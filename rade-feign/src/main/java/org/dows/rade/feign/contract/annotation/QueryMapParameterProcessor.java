package org.dows.rade.feign.contract.annotation;

import feign.MethodMetadata;
import org.dows.rade.feign.contract.AnnotatedParameterProcessor;
import org.dows.rade.feign.contract.SpringQueryMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Function:
 * <p>
 * Date: 2022/2/10 00:19
 */
public class QueryMapParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<SpringQueryMap> ANNOTATION = SpringQueryMap.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        int paramIndex = context.getParameterIndex();
        MethodMetadata metadata = context.getMethodMetadata();
        if (metadata.queryMapIndex() == null) {
            metadata.queryMapIndex(paramIndex);
            //metadata.queryMapEncoder(((SpringQueryMap) annotation).encoded());
        }
        return true;
    }

}
