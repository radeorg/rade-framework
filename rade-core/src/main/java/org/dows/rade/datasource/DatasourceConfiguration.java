package org.dows.rade.datasource;

import java.util.Properties;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/30/2024 11:38 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
public interface DatasourceConfiguration {
    String getHost();

    Integer getPort();

    String getDatabase();

    Properties getProperties();

}
