package org.dows.rade.oss.tencent.model;

import org.dows.rade.oss.OssSetting;
import org.dows.rade.oss.SliceConfig;
import org.dows.rade.oss.utils.OssPathUtil;
import lombok.Data;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/16/2022
 */
@Data
public class TencentOssConfig implements OssSetting<TencentOssConfig> {

    private String basePath;
    private String bucketName;
    private String secretId;
    private String secretKey;
    private String region;
    private String endpoint;
    /**
     * 断点续传参数
     */
    private SliceConfig sliceConfig = new SliceConfig();

    public void init() {
        this.sliceConfig.init();
        basePath = OssPathUtil.valid(basePath);
    }
}
