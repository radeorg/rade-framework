package org.dows.rade.crud;

import com.mybatisflex.core.tenant.TenantManager;

/**
 * @author Administrator
 * @data 2025/7/27 星期日
 * 忽略appId(租户ID）自动填充条件
 */
public class AppIdIgnoreUtils {
    public static void executeWithoutTenant(Runnable task) {
        try {
            TenantManager.ignoreTenantCondition();
            task.run();
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }
}
