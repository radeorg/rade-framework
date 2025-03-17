package org.dows;

import org.dows.dbo.dsl.tag.XmlParser;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/30/2024 11:14 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
public class XmlTest {

    public static void main(String[] args) {
        XmlParser.parseXml2SqlNode("<a>111<if test='true'>222<if test='true'>333</if>444<foreach collection='list' open='(' close=')' separator=',' item='item'>fff</foreach></if>555</a>");
    }
}

