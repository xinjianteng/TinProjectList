package com.tin.projectlist.app.library.reader.model.parser.txt.catalog;
/**
 * 类名： CharConverter.java<br>
 * 描述： 字符转换判断工具累<br>
 * 创建者： jack<br>
 * 创建日期：2013-10-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class CharConverter {

    public static String chinChar2UnicodeStr(char param) {
        return Integer.toHexString(param);
    }

    public static boolean isChineseCharacter(char param) {
        return param > '一' && param < 40869;
    }
}
