package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.tin.projectlist.app.library.reader.model.parser.css.AttrBorderAction;
import com.tin.projectlist.app.library.reader.model.parser.css.XHTMLStyleReader;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextBackgroundStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextBorderStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextBoxStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextFontStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntryProxy;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextWordStyleEntry;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

import java.util.ArrayList;
import java.util.Stack;


/**
 * 类名： .java<br>
 * 描述： 样式Action <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class XHTMLTagAttrAction {

    static {
        // warn the reg sequeue constant sort !!!该注册顺序不可改变
        GBTextStyleEntryProxy.reg(GBTextFontStyleEntry.class);
        GBTextStyleEntryProxy.reg(GBTextBoxStyleEntry.class);
        GBTextStyleEntryProxy.reg(GBTextBackgroundStyleEntry.class);
        GBTextStyleEntryProxy.reg(GBTextWordStyleEntry.class);
        GBTextStyleEntryProxy.reg(GBTextBorderStyleEntry.class);
    }

    public XHTMLTagAttrAction() {

    }

    final String TAG = "AttrAction";
    private String tagName = "";

    Stack<Integer> styleSpoorStack = null;// 记录样式偏移量
    int FALSE = -1; // offset <0标识添加失败
    ArrayList<GBTextStyleEntry> prepareStyle = null;
    // 是否段落内容样式
    boolean isInnerCss;
    boolean isCopyHtml = false;

    public void doAtStartProxy(XHTMLReader reader, XHTMLStyleReader styleReader, GBStringMap xmlattributes, String tag,
                               String referencePrefix, boolean isInner) {

        isInnerCss = isInner;
        this.tagName = tag;
        styleSpoorStack = reader.myModelReader.getStylePossible();
        // final TagSpoor tagSpoor=reader.tagSpoor.;

        // 最终应使用样式
        prepareStyle = styleFilter(styleReader, xmlattributes, tag, referencePrefix);
        addStyle(reader);

    }

    protected void addStyle(XHTMLReader reader) {

        if (null == prepareStyle || prepareStyle.size() == 0) {
            styleSpoorStack.push(FALSE);
        } else {
            // 将样式信息写入数据 style write...
            try {

                GBTextStyleEntryProxy proxy = new GBTextStyleEntryProxy();
                // proxy.putAll(prepareStyle);
                for (GBTextStyleEntry styleEntry : prepareStyle) {
                    proxy.put(styleEntry);
                }
                proxy.mIsInnerCss = isInnerCss;
                int ret = isInnerCss ? reader.myModelReader.addStyleEntry(proxy) : reader.myModelReader
                        .addStyleEntry2(proxy);
                styleSpoorStack.push(ret);

            } catch (Exception ex) {
                // doAtStartProxy(reader,styleReader,xmlattributes,tag,referencePrefix,isInner);
                ex.printStackTrace();
                styleSpoorStack.push(FALSE);
            }
            // L.e(TAG,"~~~~~~~~add style entry begin");

        }
    }

    protected void doAtEndProxy(XHTMLReader reader, String tag) {
        if (styleSpoorStack == null || styleSpoorStack.isEmpty()) {
            return;
        }
        if (styleSpoorStack.pop() >= 0) {
            if (isInnerCss)
                reader.myModelReader.addStyleColse();
            else
                reader.myModelReader.addStyleColse2();
            // L.e(TAG, "style-> end "+tag);
        }
    }

    /**
     *
     * 功能描述：根据样式优先级过滤样式 优先级从低到高依次是 tagStyle classStyle internalStyle
     * tagInternalStyle <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    protected ArrayList<GBTextStyleEntry> styleFilter(XHTMLStyleReader styleReader, GBStringMap xmlattributes,
                                                      String tag, String ref) {

        // 最终应使用样式 readStyleXXX读取的样式都放在CurrentEntrys里。用于优先级和冗余过滤
        ArrayList<GBTextStyleEntry> styles = styleReader.resetCurrentEntrys();
        // GBTextFontStyleEntry textStyleEntry = new GBTextFontStyleEntry();

        boolean isNull = styles == null || styles.size() == 0;
        if (!isCopyHtml) {
            // 读取标签样式
            styleReader.readStyleExternalByTAG(tag, ref);

            String htmlSelfStyAttrBorderVal = xmlattributes.getValue("border");
            if (null != htmlSelfStyAttrBorderVal) {
                styleReader.readStyleByAttr(AttrBorderAction.BORDER, htmlSelfStyAttrBorderVal);
                styleReader.addStyleInternal("td{border:1;temp:true;}");
                if (null != styles) {

                    if (isNull) {
                        isNull = styles.size() == 0;
                    }
                }
            }

            // 读取类样式
            String styleClass = xmlattributes.getValue("class");

            if (null == styleClass && isNull) {

            } else {

                if (null != styleClass) {
                    if (styleClass.contains(" ")) {

                        String[] styleClassArr = styleClass.split(" ");
                        for (String name : styleClassArr) {
                            styleReader.readStyleExternalByClass("." + name.trim(), ref, tag);
                        }

                    } else {
                        styleReader.readStyleExternalByClass("." + styleClass, ref, tag);
                    }

                    if (null != styles) {

                        if (isNull) {
                            isNull = styles.size() == 0;
                        }
                    }
                }

            }

            // 页面内style标签样式
            styleReader.readStyleInternal(tag, xmlattributes.getValue("class"), xmlattributes.getValue("id"));
            if (null != styles) {

                if (isNull) {
                    isNull = styles.size() == 0;
                }
            }
        }
        // 内嵌属性样式
        String style = xmlattributes.getValue("style");
        if (null != style) {
            // L.w(TAG, "style==" + style);

            styleReader.readStyleByAttrStr(style, ref);

            if (isNull) {
                isNull = styles == null || styles.size() == 0;
            }

        }

        /*
         * String htmlSelfStyAttrBorderVal = xmlattributes.getValue("border");
         * if (null != htmlSelfStyAttrBorderVal) {
         * styleReader.readStyleByAttr("htmlBorder", htmlSelfStyAttrBorderVal);
         * if (isNull) { isNull = htmlSelfStyAttrBorderVal == null ||
         * styles.size() == 0; } }
         */

        if (isNull) {
            return null;
        } else {
            return styles;
        }

    }

    void replace(ArrayList<GBTextStyleEntry> oldEntry, ArrayList<GBTextStyleEntry> newEntry) {

        if (null == oldEntry) {
            oldEntry = newEntry;
        }

        if (null == newEntry) {
            return;
        }

        for (int i = 0, j = oldEntry.size() - 1; i <= j; i++, j--) {

            GBTextStyleEntry entryI = oldEntry.get(i);
            GBTextStyleEntry entryJ = oldEntry.get(j);

            for (GBTextStyleEntry entry : newEntry) {
                if (entry.getClass() == entryI.getClass()) {
                    break;
                }
                if (entry.getClass() == entryJ.getClass()) {
                    break;
                }
            }
        }

    }

}
