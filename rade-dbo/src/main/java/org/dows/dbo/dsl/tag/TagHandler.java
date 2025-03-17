package org.dows.dbo.dsl.tag;

import org.dom4j.Element;
import org.dows.dbo.dsl.node.SqlNode;

import java.util.List;

public interface TagHandler {

    void handle(Element element, List<SqlNode> contents);
}
