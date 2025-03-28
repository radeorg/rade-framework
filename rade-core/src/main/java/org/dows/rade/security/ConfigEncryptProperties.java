package org.dows.rade.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * #    algorithm: PBEWithMD5AndDES
 * #    iv-generator-classname: org.jasypt.iv.NoIvGenerator
 */
@Data
@ConfigurationProperties(prefix = "rade.encryptor")
public class ConfigEncryptProperties {
    private boolean enable;
    private String secretKey;
    private String algorithm;
    private Class<?> generatorClass;


}
