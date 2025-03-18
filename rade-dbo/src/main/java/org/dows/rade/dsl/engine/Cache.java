package org.dows.rade.dsl.engine;

import org.dows.rade.dsl.node.SqlNode;

import java.util.concurrent.ConcurrentHashMap;


public class Cache {

    ConcurrentHashMap<String, SqlNode> nodeCache = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, SqlNode> getNodeCache() {
        return nodeCache;
    }
}
