package org.dows.rade.feign.contract.annotation;

import feign.MethodMetadata;
import org.dows.rade.feign.contract.AnnotatedParameterProcessor;
import org.springframework.web.bind.annotation.MatrixVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * Function:
 * <p>
 * Date: 2022/2/10 00:17
 */
public class MatrixVariableParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<MatrixVariable> ANNOTATION = MatrixVariable.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation, Method method) {
        int parameterIndex = context.getParameterIndex();
        Class<?> parameterType = method.getParameterTypes()[parameterIndex];
        MethodMetadata data = context.getMethodMetadata();
        String name = ANNOTATION.cast(annotation).value();

        checkState(emptyToNull(name) != null,
                "MatrixVariable annotation was empty on param %s.",
                context.getParameterIndex());

        context.setParameterName(name);

        if (Map.class.isAssignableFrom(parameterType)) {
            data.indexToExpander().put(parameterIndex, this::expandMap);
        } else {
            data.indexToExpander().put(parameterIndex,
                    object -> ";" + name + "=" + object.toString());
        }

        return true;
    }

    private String expandMap(Object object) {
        Map<String, Object> paramMap = (Map) object;

        return paramMap.keySet().stream()
                .map(key -> ";" + key + "=" + paramMap.get(key).toString())
                .collect(Collectors.joining());
    }

}
