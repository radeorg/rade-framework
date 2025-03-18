package org.dows.rade.dsl.tag;

import org.dom4j.Element;
import org.dows.rade.dsl.node.IfSqlNode;
import org.dows.rade.dsl.node.MixedSqlNode;
import org.dows.rade.dsl.node.SqlNode;

import java.util.List;


public class IfHandler implements TagHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        String test = element.attributeValue("test");
        if (test == null) {
            throw new RuntimeException("<if> tag missing test attribute");
        }
        List<SqlNode> contents = XmlParser.parseElement(element);

        IfSqlNode ifSqlNode = new IfSqlNode(test, new MixedSqlNode(contents));
        targetContents.add(ifSqlNode);

    }
}
