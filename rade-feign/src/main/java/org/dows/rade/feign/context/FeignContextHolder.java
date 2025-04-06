package org.dows.rade.feign.context;

/**
 * Function:
 * Date: 2022/3/21 00:10
 */
public final class FeignContextHolder {
    private static final ThreadLocal<Long> LOCAL_TIME = new ThreadLocal<>();

    /**
     * Set time
     */
    public static void setLocalTime() {
        LOCAL_TIME.set(System.currentTimeMillis());
    }

    /**
     * Get time and remove value
     *
     * @return get local time
     */
    public static Long getLocalTime() {
        Long time = LOCAL_TIME.get();
        LOCAL_TIME.remove();
        return time;
    }
}
