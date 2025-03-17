package org.dows;

import java.util.regex.Pattern;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/30/2024 11:05 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
public class RegexUtilTest {

    public static void main(String[] args) {
        boolean matches = "item".matches( "item" + "[.,:\\s\\[]");

        boolean item = Pattern.compile("item[.,:\\s\\[]").matcher("item").matches();

//        String aa = "item[0].name".replaceFirst("^\\s*" + "item" + "(?![^.,:\\s])", "aa");
//        System.out.println(aa);
        System.out.println(item);
    }
}

