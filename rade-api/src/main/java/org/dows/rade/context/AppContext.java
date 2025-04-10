package org.dows.rade.context;

public class AppContext {

    private static final ThreadLocal<String> APP_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void setAppId(String appId) {
        APP_ID_THREAD_LOCAL.set(appId);
    }

    public static String getAppId() {
        return APP_ID_THREAD_LOCAL.get();
    }

    public static void remove() {
        APP_ID_THREAD_LOCAL.remove();
    }
}
