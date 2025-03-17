package org.dows.dbo.cdc;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 7/12/2024 6:11 PM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */

@Data
public class CdcJobProperties {
    // 数据库类型[mysql,mssql]
    private String dbType;
    // 是否开启
    private boolean enable;
    // 处理类
    private String handleClass;
    // 描述
    private String descr;
    // 数据源名称
    private String datasourceName;

    //    private Map<String,String> config;
    // job名称
    private String name;
    // job的webduankou
    private Integer jobWebPort;
    // 是否加密
    private boolean encrypt;
    // 结构变化是否通知
    private boolean schemaChanges;
    // 偏移间隔
    private int offsetInterval;
    // offsetFile的文件
    private String offsetFile;
    // historyFIle的文件
    private String historyFile;
    // topic 前缀
    private String topicPrefix;
    // 服务ID
    private String serverId;
    // 连接class
    private String connectorClass;


    // 其他属性
    private Properties properties = new Properties();
    // 数据库名称 ,可以是多个，即哪些数据库需要被监听
    private Set<String> databases = new HashSet<>();
    // 表
    private Set<String> tables = new HashSet<>();


    public CdcJobProperties addDatabase(String database) {
        databases.add(database);
        return this;
    }

    public CdcJobProperties addDatabases(List<String> database) {
        databases.addAll(database);
        return this;
    }

    public CdcJobProperties addTable(String table) {
        tables.add(table);
        return this;
    }

    public CdcJobProperties addTables(List<String> table) {
        tables.addAll(table);
        return this;
    }
}

