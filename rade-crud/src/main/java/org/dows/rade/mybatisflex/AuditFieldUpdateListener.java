package org.dows.rade.mybatisflex;

import com.mybatisflex.annotation.UpdateListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 审计字段更新监听器
 * 用于自动填充创建时间、修改时间、创建人、修改人等字段
 */
@Component
public class AuditFieldUpdateListener implements UpdateListener {

    // 缓存实体类的字段信息，避免重复反射
    private final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();

    @Override
    public void onUpdate(Object entity) {
        if (entity == null) {
            return;
        }

        Class<?> clazz = entity.getClass();
        Field[] fields = getFields(clazz);

        LocalDateTime now = LocalDateTime.now();
        // 获取当前用户ID
//        UserDTO currentUser = UserContext.getCurrentUser();
//        if (currentUser != null) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    // 自动填充更新时间字段
                    if (("updateTime".equals(fieldName) || "update_time".equals(fieldName))
                            && field.getType() == LocalDateTime.class) {
                        field.set(entity, now);
                    }
                    // 自动填充更新人字段
                    else if (("updateId".equals(fieldName) || "update_id".equals(fieldName))
                            && (field.getType() == String.class || field.getType() == Long.class || field.getType() == long.class)) {
                        //field.set(entity, currentUser.getNickname());
                    }
                } catch (IllegalAccessException e) {
                    // 忽略访问异常，继续处理其他字段
                }
            }
//        }
    }

    /**
     * 获取实体类的所有字段（带缓存）
     */
    private Field[] getFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, Class::getDeclaredFields);
    }
}