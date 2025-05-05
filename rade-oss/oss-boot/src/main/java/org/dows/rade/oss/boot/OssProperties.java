package org.dows.rade.oss.boot;

import lombok.Data;
import org.dows.rade.oss.OssSetting;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "rade.oss")
public class OssProperties {

    private String endpoint;
    // 代码里是 secretId
    private String accessKey;
    private String secretKey;

    private Map<String, OssSetting> settings;
}
