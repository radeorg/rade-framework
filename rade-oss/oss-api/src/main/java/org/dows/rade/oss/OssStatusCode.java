package org.dows.rade.oss;

import org.dows.rade.status.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/16/2022
 */
public enum OssStatusCode implements StatusCode {

    DOWNLOAD_EXCEPTION,
    LIMIT_EXCEPTION,
    ;

    @Override
    public String getCode() {
        return "";
    }

    @Override
    public String getDescribe() {
        return "";
    }

}
