package org.dows.rade.exchange;

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 注解提取工具类
 * 用于按类别提取类中的注解信息
 */
public class AnnotationExtractor {


    public static Map<Class<? extends Annotation>, Map<String, Field>> extractFieldsByAnnotations(Class<?> clazz) {
        Map<Class<? extends Annotation>, Map<String, Field>> result = new HashMap<>();

        // 定义需要处理的注解类型
        Class<? extends Annotation>[] annotationTypes = new Class[]{
                UriParam.class,
                PathParam.class,
                UriBody.class,
                UriHeader.class
        };

        // 初始化结果Map
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            result.put(annotationType, new HashMap<>());
        }

        // 遍历类的所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 检查每个注解类型
            for (Class<? extends Annotation> annotationType : annotationTypes) {
                Annotation annotation = field.getAnnotation(annotationType);
                if (annotation != null) {
                    // 获取注解的value属性或使用字段名
                    String key = getFieldKey(annotation, field);
                    result.get(annotationType).put(key, field);
                }
            }
        }

        return result;
    }

    private static String getFieldKey(Annotation annotation, Field field) {
        try {
            // 尝试获取注解的value()方法
            java.lang.reflect.Method valueMethod = annotation.annotationType().getMethod("value");
            Object value = valueMethod.invoke(annotation);
            if (value instanceof String && !((String) value).isEmpty()) {
                return (String) value;
            }
        } catch (Exception e) {
            // 如果无法获取value或出现异常，使用字段名
        }
        return field.getName();
    }

    /**
     * 提取类中所有字段的注解信息，按注解类别分类
     * @param object 要提取注解的对象
     * @return 按注解类别分类的Map，key为注解类名，value为字段名和注解值的映射
     */
    public static Map<Class<? extends Annotation>, Map<String, Object>> extractFiledValueByAnnotations(Object object) {
        Map<Class<? extends Annotation>, Map<String, Object>> result = new LinkedHashMap<>();
        Class<?> clazz = object.getClass();
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 获取字段上的所有注解
            Annotation[] annotations = field.getAnnotations();
            field.setAccessible(true);
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                //String fieldName = field.getName();

                // 初始化该注解类别的Map
                result.computeIfAbsent(annotationType, k -> new LinkedHashMap<>());
                // 提取注解的值
                Object annotationValue = extractAnnotationValue(annotation);
                // 如果值为空，则取字段名
                if (annotationValue == null || StrUtil.isBlank(annotationValue.toString())) {
                    annotationValue = field.getName();
                }
                String av = annotationValue.toString();
                try {
/*                    if (annotationValue == null || StrUtil.isBlank(annotationValue.toString())) {

                    }*/
                    if (annotationType == UriHeader.class) {
                        UriHeader uriHeader = (UriHeader) annotation;
                        result.get(annotationType).put(av, uriHeader.prefix() + field.get(object));
                    } else {
                        result.get(annotationType).put(av, field.get(object));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return result;
    }
    
    /**
     * 提取类中所有字段的注解信息，按注解类别分类
     * @param clazz 要提取注解的类
     * @return 按注解类别分类的Map，key为注解类名，value为字段名和注解值的映射
     */
    public static Map<Class<? extends Annotation>, Map<String, Object>> extractAnnotations(Class<?> clazz) {
        Map<Class<? extends Annotation>, Map<String, Object>> result = new LinkedHashMap<>();
        
        if (clazz == null) {
            return result;
        }
        
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            // 获取字段上的所有注解
            Annotation[] annotations = field.getAnnotations();
            
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                String fieldName = field.getName();
                
                // 初始化该注解类别的Map
                result.computeIfAbsent(annotationType, k -> new LinkedHashMap<>());
                
                // 提取注解的值
                Object annotationValue = extractAnnotationValue(annotation);
                result.get(annotationType).put(fieldName, annotationValue);
            }
        }
        
        return result;
    }
    
    /**
     * 提取特定注解类别的信息
     * @param clazz 要提取注解的类
     * @param annotationClass 目标注解类
     * @return 字段名和注解值的映射
     */
    public static Map<String, Object> extractSpecificAnnotations(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        if (clazz == null || annotationClass == null) {
            return result;
        }
        
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                Object annotationValue = extractAnnotationValue(annotation);
                result.put(field.getName(), annotationValue);
            }
        }
        
        return result;
    }
    
    /**
     * 提取注解的值
     * @param annotation 注解对象
     * @return 注解的值
     */
    private static Object extractAnnotationValue(Annotation annotation) {
        try {
            // 尝试获取注解的value()方法
            java.lang.reflect.Method valueMethod = annotation.annotationType().getMethod("value");
            return valueMethod.invoke(annotation);
        } catch (Exception e) {
            // 如果没有value方法，返回注解的字符串表示
        }
        
        // 对于没有值的注解，返回注解的简单类名
        return annotation.annotationType().getSimpleName();
    }
    
    /**
     * 提取类级别的注解
     * @param clazz 要提取注解的类
     * @return 类级别注解的Map
     */
    public static Map<String, Object> extractClassLevelAnnotations(Class<?> clazz) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        if (clazz == null) {
            return result;
        }
        
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            String annotationType = annotation.annotationType().getSimpleName();
            Object annotationValue = extractAnnotationValue(annotation);
            result.put(annotationType, annotationValue);
        }
        
        return result;
    }
    
    /**
     * 获取所有包含特定注解的字段名
     * @param clazz 要检查的类
     * @param annotationClass 目标注解类
     * @return 包含该注解的字段名列表
     */
    public static List<String> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<String> result = new ArrayList<>();
        
        if (clazz == null || annotationClass == null) {
            return result;
        }
        
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                result.add(field.getName());
            }
        }
        
        return result;
    }
    
    /**
     * 检查字段是否包含特定注解
     * @param clazz 要检查的类
     * @param fieldName 字段名
     * @param annotationClass 目标注解类
     * @return 是否包含该注解
     */
    public static boolean hasAnnotation(Class<?> clazz, String fieldName, Class<? extends Annotation> annotationClass) {
        if (clazz == null || fieldName == null || annotationClass == null) {
            return false;
        }
        
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.isAnnotationPresent(annotationClass);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
    
    /**
     * 获取字段的特定注解值
     * @param clazz 要检查的类
     * @param fieldName 字段名
     * @param annotationClass 目标注解类
     * @return 注解值，如果不存在返回null
     */
    public static Object getAnnotationValue(Class<?> clazz, String fieldName, Class<? extends Annotation> annotationClass) {
        if (clazz == null || fieldName == null || annotationClass == null) {
            return null;
        }
        
        try {
            Field field = clazz.getDeclaredField(fieldName);
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return extractAnnotationValue(annotation);
            }
        } catch (NoSuchFieldException e) {
            // 字段不存在
        }
        
        return null;
    }
}