//package org.dows.rade.mybatis;
//
//import com.mybatisflex.core.FlexGlobalConfig;
//import com.mybatisflex.core.audit.AuditManager;
//import com.mybatisflex.core.audit.ConsoleMessageCollector;
//import com.mybatisflex.core.audit.MessageCollector;
//import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * MyBatis-Flex配置类
// *
// * @author dows
// * @version 1.0.0
// */
//@Configuration
//public class MyBatisFlexConfig implements MyBatisFlexCustomizer {
//
//    @Override
//    public void customize(FlexGlobalConfig globalConfig) {
//        // 开启审计功能
//        AuditManager.setAuditEnable(true);
//
//        // 设置 SQL 审计收集器
//        MessageCollector collector = new ConsoleMessageCollector();
//        AuditManager.setMessageCollector(collector);
//
//        // 配置逻辑删除
//        globalConfig.setLogicDeleteColumn("deleted");
//
//        // 配置乐观锁
//        globalConfig.setVersionColumn("version");
//
//        // 打印 MyBatis-Flex 的 LOGO
//        globalConfig.setPrintBanner(false);
//
//        // 注册全局插入监听器
//        globalConfig.registerInsertListener(autoSnowflakeIdInsertListener(), Object.class);
//    }
//
//    /**
//     * 自定义消息收集器
//     */
//    /**
//     * 自定义消息收集器
//     */
//    @Bean
//    public MessageCollector messageCollector() {
//        return new ConsoleMessageCollector();
//    }
//
//    /**
//     * 自动雪花ID插入监听器（带缓存）
//     */
//    @Bean
//    public AutoSnowflakeIdInsertListener autoSnowflakeIdInsertListener() {
//        return new AutoSnowflakeIdInsertListener();
//    }
//
//
////    /**
////     * 自定义雪花ID生成器
////     * 可以在这里配置workerId和dataCenterId
////     */
////    @Bean
////    public SnowFlakeIDKeyGenerator snowFlakeIDKeyGenerator() {
////        SnowFlakeIDKeyGenerator generator = new SnowFlakeIDKeyGenerator();
////        // 如果需要自定义workerId和dataCenterId，可以在这里设置
////        // generator.setWorkerId(1L);
////        // generator.setDataCenterId(1L);
////        return generator;
////    }
//}