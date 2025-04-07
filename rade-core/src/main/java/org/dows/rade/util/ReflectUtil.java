package org.dows.rade.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectUtil {
    /**
     * 判断对象是否为空
     *
     * @param obj
     */
    public static boolean isObjectNull(Object obj){
        if (obj != null) {
            Class<?> objClass = obj.getClass();
            Method[] declaredMethods = objClass.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                int methodCount = 0; // get 方法数量
                int nullValueCount = 0; // 结果为空

                for (Method declaredMethod : declaredMethods) {
                    String name = declaredMethod.getName();
                    if (name.startsWith("get") || name.startsWith("is")){
                        methodCount += 1;
                        try {
                            Object invoke = declaredMethod.invoke(obj);
                            if (invoke == null) {
                                nullValueCount += 1;
                            }
                        } catch (IllegalAccessException | InvocationTargetException e){
                            e.printStackTrace();
                        }
                    }
                }
                return methodCount == nullValueCount;
            }
        }
        return false;
    }


    public static Field getField(Class clazz, String fieldName) {
        Map<String, Field> fieldMap = getAllFieldMap(clazz);
        return fieldMap.get(fieldName);
    }

    public static Map<String, Field> getAllFieldMap(Class clazz) {
        Map<String, Field> result = new HashMap<>(16);
        List<Field> allFields = getAllFields(clazz);
        for (Field field: allFields) {
            result.put(field.getName(), field);
        }
        return result;
    }

    public static List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>(10);
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            //得到父类,然后赋给自己
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}