package org.dows.rade.aac;

public interface AacContext {
    AacUser getAacUser();

    default String getAppIdByNamespace(String namespace) {
        throw new UnsupportedOperationException("not implement!");
    }

    /*default String[] getWhitelist() {
        throw new UnsupportedOperationException("not implement!");
    }*/

    default String[] getWhitelist(String appId){
        throw new UnsupportedOperationException("not implement!");
    }
}
