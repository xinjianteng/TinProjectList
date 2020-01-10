package com.tin.projectlist.app.library.reader.model.parser.txt;

import com.core.common.GBResource;

import java.util.regex.Pattern;

/**
 * 类名： TxtCatalogRegExpMatcher.java<br>
 * 描述： txt目录匹配工具类<br>
 * 创建者： jack<br>
 * 创建日期：2013-10-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TxtCatalogRegExpMatcher {
    private String mChineseDigits = "一二三四五六七八九零十百千两１２３４５６７８９０壹贰叁肆伍陆柒捌玖拾佰仟";
    private Pattern mPattern = null;
    private String mPatternOfBodyCatalog = null;

    public TxtCatalogRegExpMatcher() {
        this.mPatternOfBodyCatalog = createRegExp();
        try {
            this.mPattern = Pattern.compile(this.mPatternOfBodyCatalog);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String createRegExp() {
        String str1 = "\\b" + "[\\u" + CharConverter.chinChar2UnicodeStr('第') + "]";
        String str2 = str1 + "[\\s]*";
        String str3 = str2 + getOrderRegularExpression();
        String str4 = str3 + "[\\s]*";
        String str5 = str4
                + RegExpressionTools.getOrExpression(GBResource.resource("readerPage").getResource("search_expression")
                .getValue());
        return str5 + "(.*?)";
    }

    private boolean findBodyCatalogElement(String paramString) {
        return mPattern == null ? false : mPattern.matcher(paramString).find();
    }

    private String getOrderRegularExpression() {
        String str = RegExpressionTools.getOrRegularExpression(RegExpressionTools.getOrExpression(this.mChineseDigits)
                + "+", "[0-9]");
        return RegExpressionTools.encapsulateWithPossibleBrackets(RegExpressionTools.encapsulateWithPossibleBlanks(str
                + "+"));
    }

    public boolean isBodyCatalogElement(String paramString) {
        return findBodyCatalogElement(paramString);
    }
}
