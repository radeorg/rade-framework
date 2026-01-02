package org.dows.rade.crud;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OrderByBuilder {
    private static final Map<Class<?>, Set<String>> ClassFieldCache = new ConcurrentHashMap<>();

    /**
     * 构建排序字段，格式为：field1:ASC,field2:DESC
     *
     * @param orderBys
     * @return
     */
    public static String build(String orderBys, Class<?> entityClass) {
        if (orderBys == null || orderBys.isEmpty()) {
            return null;
        }
        Set<String> fieldNames = ClassFieldCache.computeIfAbsent(entityClass, k -> Arrays
                .stream(ClassUtil.getDeclaredFields(entityClass)).map(Field::getName)
                .collect(java.util.stream.Collectors.toSet())
        );

        StringBuilder sb = new StringBuilder();
        String[] orderByArray = orderBys.split(",");
        for (String orderBy : orderByArray) {
            String[] orderByFields = orderBy.split(":");
            if (fieldNames.contains(orderByFields[0])) {
                sb.append(StrUtil.toUnderlineCase(orderByFields[0])).append(" ").append(orderByFields[1]).append(",");
            }
        }
        if (sb.isEmpty()) {
            return "";
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
