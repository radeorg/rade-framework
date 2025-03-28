package org.dows.rade.util;

import org.dows.rade.model.ParameterMeta;
import org.dows.rade.model.UriSignature;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MethodSignatureResolver {

    /**
     * //(Lorg/dows/rbac/api/admin/request/FindRbacGroupRequest;)Ljava/util/List<Lorg/dows/rbac/entity/RbacGroupEntity;>;
     * //public org.dows.rbac.entity.RbacGroupEntity org.dows.rbac.admin.GroupRest.getById(java.lang.Long,java.lang.String)
     * //public java.util.List org.dows.rbac.admin.GroupRest.listByQuery(org.dows.rbac.api.admin.request.FindRbacGroupRequest)
     * //public java.util.List org.dows.rbac.admin.GroupRest.listByQuery(org.dows.rbac.api.admin.request.FindRbacGroupRequest)
     * //(Lorg/dows/rbac/api/admin/request/FindRbacGroupRequest;)Ljava/util/List<Lorg/dows/rbac/entity/RbacGroupEntity;>;
     * //(Ljava/util/List<Ljava/lang/Long;>;)V
     *
     * @param method
     * @return
     */
    public static UriSignature parse(Method method) {
        UriSignature result = new UriSignature();

        // 解析返回类型
        result.setOutput(parseReturnType(method.getGenericReturnType()));

        // 解析参数类型
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        java.lang.reflect.Parameter[] parameters = method.getParameters();

        for (int i = 0; i < genericParameterTypes.length; i++) {
            ParameterMeta paramMeta = parseParameterType(genericParameterTypes[i]);
            paramMeta.setName(parameters[i].getName()); // 设置参数名
            result.getInputs().add(paramMeta);
        }

        return result;
    }

    private static ParameterMeta parseReturnType(Type returnType) {
        return parseType(returnType);
    }

    private static ParameterMeta parseParameterType(Type parameterType) {
        return parseType(parameterType);
    }

    private static ParameterMeta parseType(Type type) {
        ParameterMeta meta = new ParameterMeta();

        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;

            // 设置原始类型
            Class<?> rawType = (Class<?>) pType.getRawType();
            meta.setDataType(rawType.getName());

            // 检查是否是集合类型
            if (isCollectionType(rawType)) {
                meta.setCollectionType(rawType.getName());

                // 获取集合的泛型参数
                Type[] typeArgs = pType.getActualTypeArguments();
                if (typeArgs.length > 0) {
                    meta.setGenericType(extractGenericType(typeArgs[0]));
                }
            } else {
                // 普通泛型类
                meta.setGenericType(extractGenericType(type));
            }
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            meta.setDataType(clazz.getName());

            if (clazz.isArray()) {
                meta.setCollectionType("array");
                meta.setGenericType(clazz.getComponentType().getName());
            }
        }

        return meta;
    }

    private static String extractGenericType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return ((Class<?>) pType.getRawType()).getName();
        } else if (type instanceof Class) {
            return ((Class<?>) type).getName();
        }
        return type.getTypeName();
    }

    private static boolean isCollectionType(Class<?> type) {
        return java.util.Collection.class.isAssignableFrom(type);
    }
}