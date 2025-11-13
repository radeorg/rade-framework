package org.dows.rade.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Slf4j
@ConfigurationProperties(prefix = "rade.jackson")
public class RadeJacksonProperties {

    private Boolean timestamp;

    private Boolean bigNumberToString;

}
