//package org.dows.dbo.dds;
//
//import com.baomidou.mybatisplus.core.MybatisConfiguration;
//import com.baomidou.mybatisplus.core.config.GlobalConfig;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import lombok.RequiredArgsConstructor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.sql.DataSource;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 11/18/2024 9:50 AM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@RequiredArgsConstructor
//@Configuration
////@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
//// 排除 DataSourceAutoConfiguration 的自动配置，避免环形调用
////@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
//public class MybatisPlusDataSourceAutoConfiguration {
//
//    private final GlobalConfig globalConfig;
//
//    private final MybatisConfiguration configuration;
//
//    private final MybatisPlusInterceptor interceptor;
//
//    private final Resource[] mapperLocations;
//
//
//    /**
//     * @Bean
//     *     @ConfigurationProperties("spring.datasource.dynamic")
//     *     public DataSourceProperties dynamicDataSourceProperties() {
//     *         return new DataSourceProperties();
//     *     }
//     * @return
//     */
//    @Bean
//    @ConfigurationProperties("spring.datasource")
//    public DataSourceProperties defaultDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    public DataSource defaultDataSource() {
//        return defaultDataSourceProperties().initializeDataSourceBuilder().build();
//    }
//
//    @Bean
//    public DynamicDataSource dynamicDataSource() {
//        return new DynamicDataSource();
//    }
//
//    @Bean
//    public Map<Object, Object> dataSourceMap() {
//        Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
//        dataSourceMap.put("default", defaultDataSource());
//        return dataSourceMap;
//    }
//
//    @Bean(name = "dynamicDataSource")
//    @Qualifier("dynamicDataSource")
//    public AbstractRoutingDataSource dynamicDataSource(@Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap) {
//        DynamicDataSource dynamicDataSource = dynamicDataSource();
//        dynamicDataSource.setTargetDataSources(dataSourceMap);
//        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource());
//
//        return dynamicDataSource;
//    }
//
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap) throws Exception {
//        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dynamicDataSource(dataSourceMap));
//        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
//        sqlSessionFactoryBean.setConfiguration(configuration);
//        sqlSessionFactoryBean.setMapperLocations(mapperLocations);
//        sqlSessionFactoryBean.setPlugins(interceptor);
//
//        //sqlSessionFactoryBean.afterPropertiesSet();
//        return sqlSessionFactoryBean.getObject();
//    }
//
//
//    /**
//     * @param dynamicDataSource
//     * @return org.springframework.jdbc.datasource.DataSourceTransactionManager
//     * @Description: 将动态数据加载类添加到事务管理器
//     */
//    @Bean
//    public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
//        return new DataSourceTransactionManager(dynamicDataSource);
//    }
//}