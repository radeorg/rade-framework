package org.dows.dbo.dsl.tag;

import org.dom4j.Element;
import org.dows.dbo.dsl.node.MixedSqlNode;
import org.dows.dbo.dsl.node.SqlNode;
import org.dows.dbo.dsl.node.WhereSqlNode;

import java.util.List;


public class WhereHandler implements TagHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        WhereSqlNode node = new WhereSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
