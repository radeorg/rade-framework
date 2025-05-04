package org.dows.rade.crud;

import com.mybatisflex.core.tenant.TenantFactory;

public class AppIdFactory implements TenantFactory {
    @Override
    public Object[] getTenantIds() {
        // 返回当前应用的appId
        return new Object[]{1};
    }

//    @Bean
//    public TenantManager tenantManager() {
//        TenantManager tenantManager = new TenantManager();
//        tenantManager.setTenantFactory(new AppIdTenantFactory());
//        tenantManager.setTenantColumn("app_id");
//        return tenantManager;
//    }
}