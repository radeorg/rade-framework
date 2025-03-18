package org.dows.rade.aac;

import org.dows.rade.status.StatusCode;

public enum AuthStatusCode implements StatusCode {
    UNAUTHORIZED(),
    FORBIDDEN;

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getDescribe() {
        return null;
    }
}
