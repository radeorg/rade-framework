package org.dows.rade.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dows.rade.status.StatusCode;

/**
 * 自定义异常处理
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RadeException extends RuntimeException {

    /**
     * 返回码
     */
    protected StatusCode statusCode;
    /**
     * 异常消息参数
     */
    protected Object[] args;

    private Object data;
    private String msg;
    private String code = "500";


    public RadeException() {
    }

    public RadeException(String msg) {
        super(msg);
    }

    public RadeException(Integer code, String msg) {
        super(msg);
        extracted(code, msg);
    }

    public RadeException(Integer code, String msg, Throwable e) {
        super(msg, e);
        extracted(code, msg);
    }

    public RadeException(Object data) {
        this.data = data;
    }



    public RadeException(Throwable throwable) {
        super(throwable);
    }

    public RadeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RadeException(StatusCode statusCode) {
        super(statusCode.getDescribe());
        this.statusCode = statusCode;
    }

    public RadeException(StatusCode statusCode, Exception exception) {
        super(String.format(statusCode.getDescribe(), exception.getMessage()));
        this.statusCode = statusCode;
    }

    public RadeException(StatusCode statusCode, String msg) {
        super(String.format(statusCode.getDescribe(), msg));
        this.statusCode = statusCode;
    }

    public RadeException(StatusCode statusCode, Object[] args, String message) {
        super(message);
        this.statusCode = statusCode;
        this.args = args;
    }

    public RadeException(StatusCode statusCode, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.args = args;
    }


    private void extracted(Integer code, String msg) {
        this.statusCode = new StatusCode() {
            @Override
            public String getCode() {
                return code.toString();
            }

            @Override
            public String getDescribe() {
                return msg;
            }
        };
    }

}
