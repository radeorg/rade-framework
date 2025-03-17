package org.dows.dbo.dds;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 11/18/2024 9:50 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Configuration
public class DataSourceConfiguration {

//    @Autowired
//    @Lazy
//    private RbacDataPermissionHandler rbacDataPermissionHandler;


    /**
     * @Bean
     *     @ConfigurationProperties("spring.datasource.dynamic")
     *     public DataSourceProperties dynamicDataSourceProperties() {
     *         return new DataSourceProperties();
     *     }
     * @return
     */
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource defaultDataSource() {
        return defaultDataSourceProperties().initializeDataSourceBuilder().build();
    }


    @Bean
    public Map<Object, Object> dataSourceMap() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("default", defaultDataSource());
        return dataSourceMap;
    }

    @Bean(name = "dynamicDataSource")
    @Qualifier("dynamicDataSource")
    public AbstractRoutingDataSource dynamicDataSource(@Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource());

        return dynamicDataSource;
    }

    /**
     * @Description: 将动态数据加载类添加到事务管理器
     * @param dynamicDataSource
     * @return org.springframework.jdbc.datasource.DataSourceTransactionManager
     * 正对mybatisPlus
     *
     *     @Bean
     *     public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap) throws Exception {
     *         MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
     *         sqlSessionFactoryBean.setDataSource(dynamicDataSource(dataSourceMap));
     *         //对新的SqlSessionFactory配置 修改mybatis-plus Page自动分页失效问题 以及 找不到xml问题
     *         MybatisConfiguration configuration = new MybatisConfiguration();
     *         configuration.setMapUnderscoreToCamelCase(true);
     *         configuration.setCacheEnabled(true);
     *         configuration.setMultipleResultSetsEnabled(true);
     *         configuration.setJdbcTypeForNull(JdbcType.NULL);
     *         configuration.setLogImpl(StdOutImpl.class);
     *         GlobalConfig globalConfig = GlobalConfigUtils.defaults();
     *         globalConfig.setMetaObjectHandler(new FillHandler(aacApi));
     *         globalConfig.setDbConfig(new GlobalConfig.DbConfig().setIdType(IdType.AUTO));
     *         globalConfig.setBanner(false);
     *         sqlSessionFactoryBean.setGlobalConfig(globalConfig);
     *         sqlSessionFactoryBean.setConfiguration(configuration);
     *         sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
     *                 .getResources("classpath*:/mapper/*.xml"));
     *         MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
     *         interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
     *         interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
     *         interceptor.addInnerInterceptor(rbacDataPermissionHandler);
     *         sqlSessionFactoryBean.setPlugins(interceptor);
     *
     *         //sqlSessionFactoryBean.afterPropertiesSet();
     *         return sqlSessionFactoryBean.getObject();
     *     }
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }


    /**
     * https://blog.95id.com/dynamic-datasource-in-springboot
     * https://www.jianshu.com/p/6a1c4536fe71
     * spring:
     *   datasource:
     *     db1:
     *       driver-class-name: com.mysql.jdbc.Driver
     *       jdbc-url: jdbc:mysql://localhost:3306/db1?characterEncoding=utf8&useSSL=false
     *       username: root
     *       password: 123456
     *     db2:
     *       driver-class-name: com.mysql.jdbc.Driver
     *       jdbc-url: jdbc:mysql://localhost:3306/db2?characterEncoding=utf8&useSSL=false
     *       username: root
     *       password: 123456
     * @return
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.db1")
    public DataSource db1() {
        System.out.println("db1 creating ......");
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties("spring.datasource.db2")
    public DataSource db2() {
        System.out.println("db2 creating ......");
        return DataSourceBuilder.create().build();
    }
}