package org.dows.rade.core.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.core.id.IdGenerable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 唯一ID 组件初始化
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class IdGeneratorInit implements ApplicationRunner {

    final private IdGenerable idGenerable;

    @Override
    public void run(ApplicationArguments args) {
        idGenerable.init();
    }
}
