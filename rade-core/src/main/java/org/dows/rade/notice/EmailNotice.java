package org.dows.rade.notice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotice implements Noticer {
    @Override
    public void notice(Object notice) {
        log.info("发送邮件通知: {}", notice);
    }
}
