package org.dows.rade.dsl.tag;

import org.dom4j.Element;
import org.dows.rade.dsl.node.SqlNode;

import java.util.List;

public interface TagHandler {

    void handle(Element element, List<SqlNode> contents);
}
