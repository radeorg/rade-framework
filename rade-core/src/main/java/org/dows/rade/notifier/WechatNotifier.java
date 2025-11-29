package org.dows.rade.notifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.exchange.ExchangeClient;
import org.dows.rade.exchange.ExchangeMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WechatNotifier implements Notifier {

    private final ExchangeClient exchangeClient;

    @Override
    public <T> T notice(ExchangeMessage noticeMessage, Class<T> returnType) {
        log.info("发送微信通知: {}", noticeMessage);

        T exchange = exchangeClient.exchange(noticeMessage, returnType);
        log.info("exchange: {}", exchange);
        return exchange;

    }
}
