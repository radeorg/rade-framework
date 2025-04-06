package org.dows.rade.feign.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.dows.rade.feign.springboot.FeignSpringContextHolder;

import java.time.Duration;

/**
 * Function:
 * <p>
 * <p>
 * Date: 2022/4/27 23:19
 */
public class Metrics {
    public static void time(Duration duration, String name, String... tags) {
        MeterRegistry registry = FeignSpringContextHolder.getBean(MeterRegistry.class);
        registry.timer(name, tags).record(duration);
    }

    public static void count(String name, String... tags) {
        MeterRegistry registry = FeignSpringContextHolder.getBean(MeterRegistry.class);
        registry.counter(name, tags).increment();
    }
}
