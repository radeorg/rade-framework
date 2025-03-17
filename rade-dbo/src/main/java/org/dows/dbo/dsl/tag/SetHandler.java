package org.dows.dbo.dsl.tag;

import org.dom4j.Element;
import org.dows.dbo.dsl.node.MixedSqlNode;
import org.dows.dbo.dsl.node.SetSqlNode;
import org.dows.dbo.dsl.node.SqlNode;

import java.util.List;


public class SetHandler implements TagHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        SetSqlNode node = new SetSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
