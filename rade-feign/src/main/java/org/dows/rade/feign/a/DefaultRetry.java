package org.dows.rade.feign.a;

import feign.RetryableException;
import feign.Retryer;

import java.net.ConnectException;

public class DefaultRetry implements Retryer {
    /**
     * 最大重试次数
     */
    private final static int retryerMax = 1;
    /**
     * 当前重试次数
     */
    private int currentRetryCnt = 0;

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (currentRetryCnt > retryerMax) {
            throw e;
        }
        // 连接异常时重试
        if (e.getCause() instanceof ConnectException) {
            currentRetryCnt++;
            return;
        }
        throw e;
    }

    @Override
    public Retryer clone() {
        return new DefaultRetry();
    }
}