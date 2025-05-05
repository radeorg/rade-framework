package org.dows.rade.oss.local;

import cn.hutool.core.text.CharPool;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.dows.rade.oss.constant.OssConstant;
import org.dows.rade.oss.local.model.LocalOssConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Configuration
//@ConditionalOnClass(COSClient.class)
//@ConditionalOnBean(name = {"minioOssClient"})
@EnableConfigurationProperties({LocalOssProperties.class})
@ConditionalOnProperty(prefix = OssConstant.OSS, name = OssConstant.OssType.LOCAL + CharPool.DOT + OssConstant.ENABLE,
        havingValue = OssConstant.DEFAULT_ENABLE_VALUE, matchIfMissing = true)
public class LocalOssConfiguration implements BeanPostProcessor {

    public static final String DEFAULT_BEAN_NAME = "localOssClient";

    @Resource
    private LocalOssProperties localProperties;

    @PostConstruct
    public void localOssClient() {
        Map<String, LocalOssConfig> localOssConfigMap = localProperties.getOssConfig();
        if (localOssConfigMap.isEmpty()) {
            final LocalOssClient localOssClient = localOssClient(localProperties);
            SpringUtil.registerBean(DEFAULT_BEAN_NAME, localOssClient);
        } else {
            localOssConfigMap.forEach((name, localOssConfig) -> {
                SpringUtil.registerBean(name, localOssClient(localOssConfig));
            });
        }
    }

    public LocalOssClient localOssClient(LocalOssConfig localOssConfig) {
        return new LocalOssClient(localOssConfig);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
