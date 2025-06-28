package org.dows.rade.tree;

import org.dows.rade.exception.RadeException;
import org.dows.rade.status.CommonStatusCode;
import org.dows.rade.status.StatusCode;

public class TreeException extends RadeException {

    public TreeException() {
    }

    public TreeException(String msg) {
        super(Integer.valueOf(CommonStatusCode.FAILED.getCode()), msg);
    }

    public TreeException(Integer code, String msg) {
        super(msg);
    }

    public TreeException(Integer code, String msg, Throwable e) {
        super(msg, e);
    }

    public TreeException(Object data) {
        super(data);
    }

    public TreeException(Throwable throwable) {
        super(throwable);
    }

    public TreeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TreeException(StatusCode statusCode) {
        super(statusCode.getDescribe());
        this.statusCode = statusCode;
    }

    public TreeException(StatusCode statusCode, Exception exception) {
        super(String.format(statusCode.getDescribe(), exception.getMessage()));
        this.statusCode = statusCode;
    }

    public TreeException(StatusCode statusCode, String msg) {
        super(String.format(statusCode.getDescribe(), msg));
        this.statusCode = statusCode;
    }

    public TreeException(StatusCode statusCode, Object[] args, String message) {
        super(message);
        this.statusCode = statusCode;
        this.args = args;
    }

    public TreeException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.args = args;
    }
}
