package org.dows.rade.crud;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.context.AppContext;

@Slf4j
@RequiredArgsConstructor
public class FieldFillListener implements InsertListener, UpdateListener {
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            if (baseEntity.getAppId() == null || baseEntity.getAppId().isBlank()) {
                baseEntity.setAppId(AppContext.getAppId());
            }
        }
    }

    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            if (baseEntity.getAppId() == null || baseEntity.getAppId().isBlank()) {
                baseEntity.setAppId(AppContext.getAppId());
            }
        }
    }

    /*private String getCurrentAppId() {
        // 实现获取当前用户的逻辑
        // 例如从Spring Security上下文中获取
        //return SecurityContextHolder.getContext().getAuthentication().getName();
    }*/
}