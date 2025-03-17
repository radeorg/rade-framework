package org.dows.dbo.dsl.engine;

import org.dows.dbo.dsl.SqlMeta;
import org.dows.dbo.dsl.node.SqlNode;
import org.dows.dbo.dsl.tag.XmlParser;
import org.dows.dbo.dsl.token.TokenHandler;
import org.dows.dbo.dsl.token.TokenParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DynamicSqlEngine {

    Cache cache = new Cache();

    public SqlMeta parse(String text, Map<String, Object> params) {
        text = String.format("<root>%s</root>", text);
        SqlNode sqlNode = parseXml2SqlNode(text);
        Context context = new Context(params);
        parseSqlText(sqlNode, context);
        parseParameter(context);
        return new SqlMeta(context.getSql(), context.getJdbcParameters());
    }

    public Set<String> parseParameter(String text) {
        text = String.format("<root>%s</root>", text);
        SqlNode sqlNode = parseXml2SqlNode(text);
        HashSet<String> set = new HashSet<>();
        sqlNode.applyParameter(set);
        return set;
    }

    private SqlNode parseXml2SqlNode(String text) {
        SqlNode node = cache.getNodeCache().get(text);
        if (node == null) {
            node = XmlParser.parseXml2SqlNode(text);
            cache.getNodeCache().put(text, node);
        }
        return node;
    }

    /**
     * 解析标签，去除标签，替换 ${}为常量值, #{}保留不变
     *
     * @param sqlNode
     * @param context
     */
    private void parseSqlText(SqlNode sqlNode, Context context) {
        sqlNode.apply(context);
    }

    /**
     * #{}替换成?，并且将?对应的参数值按顺序保存起来
     *
     * @param context
     */
    private void parseParameter(Context context) {
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                Object value = context.getOgnlValue(content);
                if (value == null) {
                    throw new RuntimeException("could not found value : " + content);
                }
                context.addParameter(value);
                return "?";
            }
        });
        String sql = tokenParser.parse(context.getSql());
        context.setSql(sql);
    }
}
