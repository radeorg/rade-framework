package org.dows.rade.encrypt;

public interface EncryptApi {
    default String getBCryptPassword(String password) {
        throw new RuntimeException("未实现");
    }


}
