package org.dows.rade.oss;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OssInfo {

    /**
     * 名称
     */
    private String name;
    /**
     * 文件访问路径
     */
    private String fileLink;
    /**
     * 存储路径
     */
    private String path;
    private String filePath;
    /**
     * 对象大小
     */
    private String size;
    /**
     * 对象md5
     */
    private String md5;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 最新修改时间
     */
    private String lastUpdateTime;

}
