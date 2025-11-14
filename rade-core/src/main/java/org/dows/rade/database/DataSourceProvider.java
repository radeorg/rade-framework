package org.dows.rade.database;

import javax.sql.DataSource;

/**
 * @description: </br>
 *
 * @author: lait.zhang@gmail.com
 * @date: 11/18/2024 9:51 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
public interface DataSourceProvider {
    DataSource createDataSource(String appId);
}