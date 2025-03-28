package org.dows.rade.config;

import lombok.Data;
import org.dows.rade.init.ResourceInitializer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @description: 初始化资源</ br>
 * @author: lait.zhang@gmail.com
 * @date: 11/7/2024 6:01 PM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Data
@ConfigurationProperties(prefix = "rade.initialize")
//@ConditionalOnProperty(name = "rade.initialize.enabled", havingValue = "true")
public class InitializeProperties {
    private Boolean enabled = false;
    private List<Class<? extends ResourceInitializer>> initializers = new LinkedList<>();
    private List<Resource> resources = new ArrayList<>();

    @Data
    public static class Resource {
        private String initModel;
        private String dataDir;
        private Class<? extends ResourceInitializer> initializer;
        private List<String> scanPackages;
    }

}
