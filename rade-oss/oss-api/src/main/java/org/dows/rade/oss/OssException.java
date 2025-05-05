package org.dows.rade.oss;


import org.dows.rade.exception.RadeException;
import org.dows.rade.status.StatusCode;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/16/2022
 */
public class OssException extends RadeException {


    public OssException(String msg) {
        super(msg);
    }

    public OssException(Throwable throwable) {
        super(throwable);
    }

    public OssException(StatusCode statusCode) {
        super(statusCode);
    }

    public OssException(StatusCode statusCode, String msg) {
        super(statusCode, msg);
    }

    public OssException(StatusCode statusCode, Exception exception) {
        super(statusCode, exception);
    }


}
