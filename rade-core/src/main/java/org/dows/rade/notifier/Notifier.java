package org.dows.rade.notifier;

import org.dows.rade.exchange.ExchangeMessage;

public interface Notifier {

    /**
     * 发送通知
     *
     * @param notice     通知内容
     * @param returnType
     */
    <T> T notice(ExchangeMessage notice, Class<T> returnType);
}
