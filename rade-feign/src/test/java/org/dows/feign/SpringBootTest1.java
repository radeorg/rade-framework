package org.dows.feign;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.feign.register.EnableFeignPlusClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test0")
@SpringBootTest(classes = SpringBootTest1.class)
@EnableFeignPlusClients(basePackages = "org.dows.feign.test")
@EnableAutoConfiguration
@Slf4j
public class SpringBootTest1 {

    @Resource
    private Github github;

    @Test
    public void test1() {
        List<GitHubRes> contributors = github.contributors("geeker-lait", "rade-framework");
//        log.info("contributors={}", new Gson().toJson(contributors));
    }
}
