package org.dows.rade.notifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.exchange.ExchangeMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {

    @Override
    public <T> T notice(ExchangeMessage exchangeMessage, Class<T> returnType) {

        log.info("发送邮件通知: {}", exchangeMessage);
        return null;
    }
}
