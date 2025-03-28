package org.dows.rade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 文件
 */
@Data
//@Configuration
@ConfigurationProperties(prefix = "rade.upload")
public class FileProperties {
    // 上传模式
    private FileModeEnum mode;
    // 上传类型
    private String type;
    // 本地文件上传
    @NestedConfigurationProperty
    private LocalFileProperties local;
}
