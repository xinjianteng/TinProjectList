package com.tin.projectlist.app.library.reader.model.parser.txt;
/**
 * 类名： RegExpressionTools.java<br>
 * 描述： 正则表达式组织工具类<br>
 * 创建者： jack<br>
 * 创建日期：2013-10-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class RegExpressionTools {
    public static String encapsulateWithPossibleBlanks(String paramString) {
        String str = "[\\s]*" + paramString;
        return str + "[\\s]*";
    }

    public static String encapsulateWithPossibleBrackets(String paramString) {
        String str = "[\\(|\\[]*" + paramString;
        return str + "[\\)|\\]]*";
    }
    /**
     * 功能描述： 获取或关系正则<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-18<br>
     *
     * @param paramString 要分解的字符串
     * @return
     */
    public static String getOrExpression(String paramString) {
        if ((paramString == null) || (paramString.length() == 0))
            System.out.println("empty input for getOrRegularExpression!");
        String str2 = "[" + "\\u" + CharConverter.chinChar2UnicodeStr(paramString.charAt(0));
        int i = 1;
        int j = paramString.length();
        while (i < j) {
            String str3 = str2 + "|";
            str2 = str3 + "\\u" + CharConverter.chinChar2UnicodeStr(paramString.charAt(i));
            ++i;
        }
        return str2 + "]";
    }
    /**
     * 功能描述： 获取或关系正则<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-18<br>
     *
     * @param paramString1
     * @param paramString2
     * @return
     */
    public static String getOrRegularExpression(String paramString1, String paramString2) {
        return "[" + paramString1 + "|" + paramString2 + "]";
    }
}
