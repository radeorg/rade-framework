package org.dows.rade.dsl.tag;

import org.dom4j.Element;
import org.dows.rade.dsl.node.MixedSqlNode;
import org.dows.rade.dsl.node.SetSqlNode;
import org.dows.rade.dsl.node.SqlNode;

import java.util.List;


public class SetHandler implements TagHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        SetSqlNode node = new SetSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
