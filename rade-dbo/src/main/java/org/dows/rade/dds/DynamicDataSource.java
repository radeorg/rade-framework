package org.dows.rade.dds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setAppDataSource(String appid) {
        Assert.notNull(appid, "appid`s dataSource type must not be null");
        contextHolder.set(appid);
    }

    public static String getDataSourceType() {
        return contextHolder.get();
    }

    public static void clearAppDataSource() {
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceType();
    }


}