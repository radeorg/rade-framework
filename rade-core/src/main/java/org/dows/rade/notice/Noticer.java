package org.dows.rade.notice;

public interface Noticer {

    /**
     * 发送通知
     *
     * @param notice 通知内容
     */
    void notice(Object notice);
}
