package org.dows.rade.mybatis;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import org.dows.rade.annotation.AutoSnowflakeId;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自动雪花ID插入监听器（带缓存优化）
 * 用于处理所有带有@AutoSnowflakeId注解的字段
 */
public class AutoSnowflakeIdInsertListener implements InsertListener {

    // 缓存实体类的字段信息，避免重复反射
    private final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();
    private final Map<String, Field> annotatedFieldCache = new ConcurrentHashMap<>();

    // 使用MyBatis-Flex自带的雪花ID生成器
    private final SnowFlakeIDKeyGenerator snowflakeGenerator = new SnowFlakeIDKeyGenerator();

    @Override
    public void onInsert(Object entity) {
        if (entity == null) {
            return;
        }

        Class<?> clazz = entity.getClass();
        Field[] annotatedFields = getAnnotatedFields(clazz);

        for (Field field : annotatedFields) {
            try {
                field.setAccessible(true);
                Object currentValue = field.get(entity);

                // 如果当前值为空，则自动填充雪花ID
                if (currentValue == null || (field.getType() == long.class && (Long) currentValue == 0L)) {
                    Long snowflakeId = (Long) snowflakeGenerator.generate(null, null);
                    field.set(entity, snowflakeId);
                }
            } catch (IllegalAccessException e) {
                // 忽略访问异常，继续处理其他字段
            }
        }
    }

    /**
     * 获取带有@AutoSnowflakeId注解的字段（带缓存）
     */
    private Field[] getAnnotatedFields(Class<?> clazz) {
        String cacheKey = clazz.getName();

        // 先从缓存中获取
        if (annotatedFieldCache.containsKey(cacheKey)) {
            Field field = annotatedFieldCache.get(cacheKey);
            return field != null ? new Field[]{field} : new Field[0];
        }

        // 获取所有字段（带缓存）
        Field[] fields = fieldCache.computeIfAbsent(clazz, Class::getDeclaredFields);

        // 查找带有@AutoSnowflakeId注解的字段
        Field annotatedField = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoSnowflakeId.class)) {
                // 检查字段类型是否为Long
                if (field.getType() == Long.class || field.getType() == long.class) {
                    annotatedField = field;
                    break;
                }
            }
        }

        // 缓存结果
        annotatedFieldCache.put(cacheKey, annotatedField);

        return annotatedField != null ? new Field[]{annotatedField} : new Field[0];
    }

    /**
     * 清空缓存（用于开发环境热部署等场景）
     */
    public void clearCache() {
        fieldCache.clear();
        annotatedFieldCache.clear();
    }

    
    /*// 缓存实体类的字段信息，避免重复反射
    private final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();
    private final Map<String, Field> annotatedFieldCache = new ConcurrentHashMap<>();
    
    @Override
    public void onInsert(Object entity) {
        if (entity == null) {
            return;
        }
        
        Class<?> clazz = entity.getClass();
        Field[] annotatedFields = getAnnotatedFields(clazz);

        for (Field field : annotatedFields) {
            try {
                field.setAccessible(true);
                Object currentValue = field.get(entity);
                
                // 如果当前值为空，则自动填充雪花ID
                if (currentValue == null || (field.getType() == long.class && (Long) currentValue == 0L)) {
                    field.set(entity, SnowflakeIdGenerator.generateId());
                }
            } catch (IllegalAccessException e) {
                // 忽略访问异常，继续处理其他字段
            }
        }
    }
    
    *//**
     * 获取带有@AutoSnowflakeId注解的字段（带缓存）
     *//*
    private Field[] getAnnotatedFields(Class<?> clazz) {
        String cacheKey = clazz.getName();
        
        // 先从缓存中获取
        if (annotatedFieldCache.containsKey(cacheKey)) {
            Field field = annotatedFieldCache.get(cacheKey);
            return field != null ? new Field[]{field} : new Field[0];
        }
        
        // 获取所有字段（带缓存）
        Field[] fields = fieldCache.computeIfAbsent(clazz, Class::getDeclaredFields);
        
        // 查找带有@AutoSnowflakeId注解的字段
        Field annotatedField = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoSnowflakeId.class)) {
                // 检查字段类型是否为Long
                if (field.getType() == Long.class || field.getType() == long.class) {
                    annotatedField = field;
                    break;
                }
            }
        }
        
        // 缓存结果
        annotatedFieldCache.put(cacheKey, annotatedField);
        
        return annotatedField != null ? new Field[]{annotatedField} : new Field[0];
    }
    
    *//**
     * 清空缓存（用于开发环境热部署等场景）
     *//*
    public void clearCache() {
        fieldCache.clear();
        annotatedFieldCache.clear();
    }*/
}