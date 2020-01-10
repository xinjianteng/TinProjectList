package com.tin.projectlist.app.library.reader.model.parser.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.core.css.CSSParser;
import com.core.css.StyleBlock;
import com.core.file.GBFile;
import com.core.log.L;
import com.core.text.model.style.GBTextFontStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;
import com.core.xml.GBStringMap;
import com.geeboo.read.model.bookmodel.TagSpoor;

/**
 *
 * 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-27<br>
 * 版本： <br>
 * com.geeboo.read.model.parser.css 修改者： <br>
 * 修改日期：<br>
 * 生命周期与一本书相同：加载外部引用样式文件里的样式 一本epub文件读取完毕则清空样式文件里的样式
 * 内嵌和内部类样式与epub文件里的一个xhtml生命周期相同， 内嵌和内部样式直接将Attr转换为StyleBlock
 * 内嵌和内部类样式将Attr转换为StyleBlock 同时 在一个xhtml文件开始时保存在内存里，结束时释放掉内存里的样式信息 提供按标签查找样式的方法
 */
public class XHTMLStyleReader {

    public XHTMLStyleReader() {
        fillAttrTable();
    }

    private static final HashMap<String, AttrAction> ourAttrActions = new HashMap<String, AttrAction>();

    AttrAction ourNullAction = new AttrAction() {

        @Override
        protected GBTextStyleEntry create(String attrName, String attrVal) {
            return null;
        }

        @Override
        protected void doIt(String attrName, String attrVal, GBTextStyleEntry entry) {

        }

        @Override
        protected Class<?> getEntryType() {
            return null;
        }

        @Override
        protected boolean isAlwaysNew() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        protected void setAlwaysNew(boolean isAlwaysNew) {
            // TODO Auto-generated method stub

        }

    };

    private final HashMap<String, AttrAction> myActions = new HashMap<String, AttrAction>();

    private AttrAction getTagAction(String tag) {
        return ourAttrActions.get(tag.toLowerCase());
        /*
         * AttrAction action = myActions.get(tag); if (action == null) { action
         * = ourAttrActions.get(tag.toLowerCase()); if (action == null) { action
         * = ourNullAction; } myActions.put(tag, action); } return action ==
         * ourNullAction ? null : action;
         */
    }

    final String TAG = "XHTMLStyleReader";

    // 内部样式 适用于某个html文件的内部样式或内嵌样式 key 可以是样式属性名字 生命周期 一个页面结束
    ArrayList<StyleBlock> styleInternal = new ArrayList<StyleBlock>();
    // 内部类样式
    HashMap<String, String> styleClassInternal = new HashMap<String, String>();
    // 外部样式 适用于某个html文件引入的外部css的样式 包含 类、标签、Id、样式类型信息
    static HashMap<String, ArrayList<StyleBlock>> styleExternal = new HashMap<String, ArrayList<StyleBlock>>();
    // 外部类样式
    // static HashMap<String, HashMap<String, String>> styleClassExternal = new
    // HashMap<String, HashMap<String, String>>();
    // 索引列表
    static ArrayList<ReferencePrefix> referencePrefixList = new ArrayList<ReferencePrefix>();

    private ArrayList<GBTextStyleEntry> currentEntrys = new ArrayList<GBTextStyleEntry>();

    /**
     *
     * 功能描述：获取当前已读样式列表 创建者： yangn<br>
     * 创建日期：2013-5-29<br>
     *
     * @param
     */
    public ArrayList<GBTextStyleEntry> getCurrentEntrys() {
        return currentEntrys;
    }

    /**
     *
     * 功能描述： 清空当前样式信息 创建者： yangn<br>
     * 创建日期：2013-5-29<br>
     *
     * @param
     */
    public ArrayList<GBTextStyleEntry> resetCurrentEntrys() {
        if (null == currentEntrys) {
            currentEntrys = new ArrayList<GBTextStyleEntry>();
        } else {
            currentEntrys.clear();
        }
        return currentEntrys;
    }

    /*
     * public void init(String path,String referencePrefix){ //加载样式文件到内存 //将 }
     */

    /**
     *
     * 功能描述：加载外部样式文件<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     * @throws IOException
     */
    public void doIt(String path) throws IOException {

        fillAttrTable();
        GBFile file = GBFile.createFileByPath(path);
        if (null == file || !file.exists()) {
            L.d(TAG, "file no  exists" + path);
            return;
        }

        L.d(TAG, path + "file exists");
        InputStream in = file.getInputStream();
        L.d(TAG, "available=" + in.available());

        final String encoding = "utf-8";
        InputStreamReader isr = new InputStreamReader(in, encoding);

        CSSParser parser = new CSSParser(isr);
        ArrayList<StyleBlock> styleEntryList = parser.getStyleBlocks();
        styleExternal.put(path, styleEntryList);
    }

    /*
     * static String getColorVal(String color) { StringBuilder sb = new
     * StringBuilder(color); sb.delete(0, 1); sb.delete(3, 5); sb.delete(6, 8);
     * sb.delete(9, 10); // System.out.println(sb.toString()); return
     * sb.toString(); // return Integer.parseInt(sb.toString()); }
     */

    /**
     *
     * 功能描述：加载内部样式 放在xhtml head标签内的 <br>
     * ]创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param referencePrefix 文件索引名称
     */
    public void loadStyleInternal(String referencePrefix) {
    }

    /**
     *
     * 功能描述：获取内部样式（即html style 标签内样式） <br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-17<br>
     *
     * @param
     */
    public void readStyleInternal(String tag, String className, String id) {
        if (this.styleInternal.isEmpty()) {
            return;
        }
        if (tag != null && !"".equals(tag)) {
            addStyle(this.styleInternal, tag, READ_TAG, "");
        }

        if (className != null && !"".equals(className)) {
            addStyle(this.styleInternal, className, READ_CLASS, tag);
        }

    }

    public boolean addStyleInternal(String styleInfo) {
        if (null == styleInfo || "".equals(styleInfo)) {
            return false;
        }

        StringReader reader = new StringReader(styleInfo);
        try {
            CSSParser parser = new CSSParser(reader);
            ArrayList<StyleBlock> styleBlocks = parser.getStyleBlocks();
            if (null == styleBlocks) {
                return false;
            }
            styleInternal.addAll(styleBlocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void removeTempStyleInternal() {
        if (null == styleInternal) {
            return;
        }
        for (StyleBlock block : styleInternal) {
            if (block.Name.equalsIgnoreCase("td") && block.Info.getValue("temp") != null
                    && block.Info.getValue("temp").equalsIgnoreCase("true")) {
                styleInternal.remove(block);
            }
        }
    }

    /**
     * 功能描述： 添加外部样式 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param href type rel
     */
    public boolean addStyleExternal(String referencePrefix, String href, String rel, String type) {

        ReferencePrefix prefix = null;
        if ((prefix = existsStyleExternalRefPrefix(referencePrefix)) == null) {
            prefix = new ReferencePrefix(referencePrefix, href, rel, type);
            referencePrefixList.add(prefix);
        }

        if (!styleExternal.containsKey(href)) {
            try {
                doIt(href);
            } catch (IOException e) {
                return false;
            }
        }

        /*
         * boolean isExists = false; for (CSSFileEntry entry :
         * prefix.getCSSFileEntrys()) { if (entry.Href.equals(href)) { isExists
         * = true; break; } } if (!isExists) { prefix.putLink(new
         * CSSFileEntry(href, rel, type)); }
         */

        return true;
    }

    public boolean addStyleExternal(String styleInfo) {
        return true;
    }

    /**
     *
     * 功能描述：判断样式文件是否存在 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    public ReferencePrefix existsStyleExternalRefPrefix(String referencePrefix) {

        if (null == referencePrefix || null == referencePrefixList) {
            return null;
        }

        ReferencePrefix prefix = null;
        for (int i = 0, j = referencePrefixList.size() - 1; i <= j; i++, j--) {
            prefix = referencePrefixList.get(i);
            if (prefix.ReferencePrefix.equals(referencePrefix)) {
                break;
            }

            prefix = referencePrefixList.get(j);
            if (prefix.ReferencePrefix.equals(referencePrefix)) {
                break;
            }

        }

        return prefix;
    }

    /**
     *
     * 功能描述：获取CSS引用信息 创建者： yangn<br>
     * 创建日期：2013-5-23<br>
     *
     * @param
     */
    private ReferencePrefix getRef(String referencePrefix) {
        ReferencePrefix ref = null;
        for (int i = 0, j = referencePrefixList.size() - 1; i <= j; i++, j--) {
            ref = referencePrefixList.get(i);
            if (ref.ReferencePrefix.equals(referencePrefix)) {
                break;
            }

            ref = referencePrefixList.get(j);
            if (ref.ReferencePrefix.equals(referencePrefix)) {
                break;
            }

        }
        return ref;
    }

    /**
     *
     * 功能描述：执行action 创建者： yangn<br>
     * 创建日期：2013-5-23<br>
     *
     * @param 属性集合
     */
    private void execAction(GBStringMap set) {// ,
        // ArrayList<AttrAction>
        // actionDoList
        // ArrayList<GBTextStyleEntry> entrys=new ArrayList<GBTextStyleEntry>();
        // currentEntrys=new ArrayList<GBTextStyleEntry>();
        HashSet<AttrAction> noPossibleAlwaysNewList = new HashSet<AttrAction>();
        AttrAction action = null;

        String[] keys = set.getKeys();
        String[] vals = set.getVals();

        GBTextStyleEntry currentStyle = null;
        for (int i = 0; i < keys.length; i++) {

            if (null == keys[i] || "".equals(keys[i])) {
                break;
            }
            String key = keys[i].trim();
            action = getTagAction(key);

            if (action == null) {// attributesexec
                continue;
            }

            if (null == currentStyle || currentStyle.getClass() != action.getEntryType()) {
                currentStyle = null;
                for (int j = currentEntrys.size() - 1; j >= 0; j--) {
                    if (currentEntrys.get(j).getClass() == action.getEntryType()) {
                        currentStyle = currentEntrys.get(j);
                        break;
                    }
                }
            }

            if (null == currentStyle) {
                currentStyle = action.create(key, vals[i]);
                currentEntrys.add(currentStyle);

                /*
                 * if(action.isAlwaysNew()){
                 * noPossibleAlwaysNewList.add(action); }
                 */

            } else {
                /*
                 * if (!noPossibleAlwaysNewList.contains(action) &&
                 * action.isAlwaysNew()) { action.setAlwaysNew(false);
                 * noPossibleAlwaysNewList.add(action);
                 * //currentEntrys.remove(currentStyle); //currentEntrys=null;
                 * //L.e(TAG, key + "  v==" + vals[i]); currentStyle
                 * =action.create(key, vals[i]);
                 * currentEntrys.add(currentStyle); } else {
                 */

                action.doIt(key, vals[i], currentStyle);
                // }

            }
        }

        for (AttrAction itemAction : noPossibleAlwaysNewList) {
            itemAction.setAlwaysNew(true);
        }

    }

    /** 读标签样式 */
    static final byte READ_TAG = 1;
    /** 读类样式 */
    static final byte READ_CLASS = READ_TAG + 1;
    /** 读ID样式 */
    static final byte READ_ID = READ_TAG + 2;

    /**
     * 功能描述： 获取外部标签标签样式 example: H1{color:red} <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    public void readStyleExternalByTAG(String tagName, String referencePrefix) {
        readStyleExternal(tagName, referencePrefix, READ_TAG, "");
    }

    /**
     * 功能描述： 获取外部类样式,读取类样式 example: .className{color:red} <html
     * class="className"/> 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    public void readStyleExternalByClass(String styleInfo, String referencePrefix, String tagName) {

        readStyleExternal(styleInfo, referencePrefix, READ_CLASS, tagName);
    }

    /**
     * 功能描述：读取外部样式 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    protected void readStyleExternal(String styleKey, String referencePrefix, int readCase, String tagName) {

        if (null == styleExternal || 0 == styleExternal.size()) {
            return;
        }

        ReferencePrefix ref = getRef(referencePrefix);
        if (null == ref) {
            return;
        }

        // 获取当前应用样式
        ArrayList<StyleBlock> styleBlockList = null;
        // ArrayList<AttrAction> actionDoListAll = new ArrayList<AttrAction>();

        for (int i = 0; i < ref.getCSSFileEntrys().size(); i++) {
            // ArrayList<AttrAction> actionDoList = new ArrayList<AttrAction>();
            styleBlockList = styleExternal.get(ref.getCSSFileEntrys().get(i).Href);
            addStyle(styleBlockList, styleKey, readCase, tagName);
        }
        // for (CSSFileEntry fileEntry : ref.getCSSFileEntrys()) {
        // // ArrayList<AttrAction> actionDoList = new ArrayList<AttrAction>();
        // styleBlockList = styleExternal.get(fileEntry.Href);
        // addStyle(styleBlockList, tagName, readCase);
        // }

    }

    /**
     *
     * 功能描述：添加样式 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-17<br>
     *
     * @param
     */
    private void addStyle(ArrayList<StyleBlock> styleBlockList, String styleKey, int readCase, String tag) {

        if (null == styleBlockList || styleBlockList.size() == 0) {
            return;
        }
        StyleBlock styleBlock = null;
        for (int i = 0; i < styleBlockList.size(); i++) {// StyleBlock
            // styleBlock :
            // styleBlockList) {
            styleBlock = styleBlockList.get(i);
            if (null == styleBlock) {

                L.d(TAG, "current style block is " + styleBlock);
                continue;
            }

            switch (readCase) {
                case READ_TAG :
                    if (styleBlock.getType() == StyleBlock.TAG) {
                        // L.e(TAG, "style block name==" +
                        // styleBlock.Name + " tagName=" + tagName);

                        if (styleKey.equals(styleBlock.Name)
                        /* 不处理嵌套标签（父级节点样式）|| TagSpoor.endsWith(styleBlock.Name) */
                                || splitDot(styleKey, styleBlock.Name)) {
                            execAction(styleBlock.Info);// ,

                            break;
                        }
                    }
                    break;
                case READ_CLASS :
                    if (styleBlock.getType() == StyleBlock.CLASS) {

                        if (styleKey.equals(styleBlock.Name) || splitDot(styleKey, styleBlock.Name)) {
                            execAction(styleBlock.Info);
                            break;
                        } else if (!"".equals(tag)) {
                            if ((tag + styleKey).equals(styleBlock.Name) || splitDot(tag + styleKey, styleBlock.Name)) {
                                execAction(styleBlock.Info);
                                break;
                            }
                        }
                    }
                    break;
                case READ_ID :
                    if (styleBlock.getType() == StyleBlock.ID) {

                        if (styleKey.equals(styleBlock.Name)) {
                            execAction(styleBlock.Info);
                            break;
                        }
                    }
                    break;
            }
        }

    }

    public void readStyleByAttr(String key, String val) {
        if (null == key || null == val) {
            return;
        }
        readStyleByAttrStr(key + ":" + val, null);

    }

    /**
     * 功能描述： 获取内嵌样式 按属性获取样式example: <html style='color:red'/> <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    public void readStyleByAttrStr(String styleInfo, String referencePrefix) throws NullPointerException {
        if (null == styleInfo) {
            throw new NullPointerException("readStyleByAttr param styleInfo is null");
        }
        StringReader reader = new StringReader(styleInfo);
        try {
            CSSParser parser = new CSSParser(reader);
            StyleBlock styleBlock = parser.getStyleBlock();
            if (null == styleBlock) {
                return;
            }

            // ArrayList<AttrAction> actionDoList = new ArrayList<AttrAction>();
            // ArrayList<GBTextStyleEntry> styleEntryList=
            execAction(styleBlock.Info);

            return;
            /*
             * if (actionDoList.size() == 0) { return null; } else {
             * ArrayList<GBTextStyleEntry> styleEntryList = new
             * ArrayList<GBTextStyleEntry>(); for (AttrAction action :
             * actionDoList) { styleEntryList.add(action.getStyleEntry()); }
             * return styleEntryList; }
             */
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     *
     * 功能描述：id选择器样式 创建者： yangn<br>
     * 创建日期：2013-4-28<br>
     *
     * @param
     */
    public void readStyleById(GBTextFontStyleEntry GBTextStyleEntry, String styleInfo, String referencePrefix) {
        return;
    }

    /**
     * 功能描述：清除内部样式 放在xhtml head标签内的 创建者： yangn<br>
     * 创建日期：2013-4-26<br>
     *
     * @param
     */
    public void clearStyleInternal() {

        if (null != styleInternal) {
            styleInternal.clear();
        }

        if (null != styleClassInternal) {
            styleClassInternal.clear();
        }
    }

    /**
     *
     * 功能描述：tagName 是否包含在以逗号分割格式的blockName <br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-18<br>
     *
     * @param
     */
    public boolean splitDot(String tagName, String blockName) {

        if (blockName.indexOf(",") < 0) { // NumUtil.indexOf(blockName, ',') ==
            // -1
            return false;
        }
        String[] arr = blockName.split(",");
        if (arr.length <= 1) {
            return false;
        } else {

            for (int i = 0, j = arr.length - 1; i < j; ++i, --j) {
                if (arr[i].equals(tagName)) {
                    return true;
                } else if (arr[j].equals(tagName)) {
                    return true;
                } else if (arr[i].indexOf(" ") > 0) {// NumUtil.indexOf(arr[i],
                    // ' ') != -1) {
                    // 逗号分割的嵌套样式
                    return TagSpoor.endsWith(arr[i]);
                } else if (arr[j].indexOf(" ") > 0) {// NumUtil.indexOf(arr[j],
                    // ' ') != -1) {
                    // 逗号分割的嵌套样式
                    return TagSpoor.endsWith(arr[j]);
                }
            }
            /*
             * for (int i=0;i<=arr.length-1;i++) { if (str.equals(tagName)) {
             * return true; } }
             */
            return false;
        }

    }

    /**
     *
     * 功能描述：清空外部样式 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     *
     * @param
     */
    public static void clearStyleExternal() {
        if (null != styleExternal) {
            styleExternal.clear();
        }
        if (null != referencePrefixList) {
            referencePrefixList.clear();
        }
    }

    /*
     * class StyleEntry { // enum static final byte TAG = 1; static final byte
     * CLASS = 2; static final byte ID = 3; // properties byte Type; String
     * Name; HashMap<String, String> Info = new HashMap<String, String>(); }
     */
    public static void addAction(String tag, AttrAction action) {
        // AttrAction old = ourAttrActions.get(tag);
        ourAttrActions.put(tag, action);
        // return old;
    }

    public static void fillAttrTable() {

        if (!ourAttrActions.isEmpty()) {
            return;
        }
        AttrFontAction fontAction = new AttrFontAction();
        // xmlAttribute
        // font
        addAction(AttrFontAction.COLOR, fontAction);
        addAction(AttrFontAction.FONT_SIZE, fontAction);
        addAction(AttrFontAction.FAMILY, fontAction);
        addAction(AttrFontAction.LINE_HEIGHT, fontAction);
        addAction(AttrFontAction.FONT_WEIGHT, fontAction);

        // background
        AttrBackgroundAction backgroundAction = new AttrBackgroundAction();
        addAction(AttrBackgroundAction.BACKGROUND_COLOR, backgroundAction);
        addAction(AttrBackgroundAction.BACKGROUND_IMG, backgroundAction);

        // box
        AttrBoxAction boxAction = new AttrBoxAction();
        addAction(AttrBoxAction.MAGRIN, boxAction);
        addAction(AttrBoxAction.MAGRIN_TOP, boxAction);
        addAction(AttrBoxAction.MAGRIN_RIGTH, boxAction);
        addAction(AttrBoxAction.MAGRIN_BOTTOM, boxAction);
        addAction(AttrBoxAction.MAGRIN_LEFT, boxAction);
        addAction(AttrBoxAction.PADDING, boxAction);
        addAction(AttrBoxAction.PADDING_TOP, boxAction);
        addAction(AttrBoxAction.PADDING_RIGHT, boxAction);
        addAction(AttrBoxAction.PADDING_BOTTOM, boxAction);
        addAction(AttrBoxAction.PADDING_LEFT, boxAction);

        // word
        AttrWordAction wordAction = new AttrWordAction();
        addAction(AttrWordAction.TEXT_ALIGN, wordAction);
        addAction(AttrWordAction.TEXT_INDENT, wordAction);
        // border

        AttrBorderAction borderAction = new AttrBorderAction();

        addAction(AttrBorderAction.BORDER, borderAction);
        addAction(AttrBorderAction.HTM_BORDER, borderAction);
        addAction(AttrBorderAction.BORDER_COLOR, borderAction);
        addAction(AttrBorderAction.BORDER_STYLE, borderAction);
        addAction(AttrBorderAction.BORDER_WIDTH, borderAction);

        // bottom
        addAction(AttrBorderAction.BORDER_BOTTOM, borderAction);
        addAction(AttrBorderAction.BORDER_BOTTOM_COLOR, borderAction);
        addAction(AttrBorderAction.BORDER_BOTTOM_STYLE, borderAction);
        addAction(AttrBorderAction.BORDER_BOTTOM_WIDTH, borderAction);
        // right
        addAction(AttrBorderAction.BORDER_RIGHT, borderAction);
        addAction(AttrBorderAction.BORDER_RIGHT_COLOR, borderAction);
        addAction(AttrBorderAction.BORDER_RIGHT_STYLE, borderAction);
        addAction(AttrBorderAction.BORDER_RIGHT_WIDTH, borderAction);
        // left
        addAction(AttrBorderAction.BORDER_LEFT, borderAction);
        addAction(AttrBorderAction.BORDER_LEFT_COLOR, borderAction);
        addAction(AttrBorderAction.BORDER_LEFT_STYLE, borderAction);
        addAction(AttrBorderAction.BORDER_LEFT_WIDTH, borderAction);
        // top
        addAction(AttrBorderAction.BORDER_TOP, borderAction);
        addAction(AttrBorderAction.BORDER_TOP_COLOR, borderAction);
        addAction(AttrBorderAction.BORDER_TOP_STYLE, borderAction);
        addAction(AttrBorderAction.BORDER_TOP_WIDTH, borderAction);

    }

    /**
     *
     * 类名： .java<br>
     * 描述：CSS文件描述类 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    class CSSFileEntry {

        public CSSFileEntry(String href, String rel, String type) {
            this.Href = href;
            this.Rel = rel;
            this.Type = type;
        }

        String Href = "".intern();
        String Rel = "".intern();
        String Type = "".intern();
    }

    /**
     *
     * 类名： .java<br>
     * 描述：xhtml文件索引信息类（包含xhtml文件的外链接样式文件信息） <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-27<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    class ReferencePrefix {

        String ReferencePrefix = "";

        public ReferencePrefix(String referencePrefix, String href, String rel, String type) {
            this.ReferencePrefix = referencePrefix;
            cssFileEntrys.add(new CSSFileEntry(href, rel, type));
        }

        ArrayList<CSSFileEntry> cssFileEntrys = new ArrayList<CSSFileEntry>();

        /**
         *
         * 功能描述：添加外部链接样式文件信息 创建者： yangn<br>
         * 创建日期：2013-4-27<br>
         *
         * @param
         */
        void putLink(CSSFileEntry cssFileEntry) {
            cssFileEntrys.add(cssFileEntry);
        }

        /**
         *
         * 功能描述：获取外部链接样式文件信息 创建者： yangn<br>
         * 创建日期：2013-4-27<br>
         *
         * @param
         */
        public ArrayList<CSSFileEntry> getCSSFileEntrys() {
            return cssFileEntrys;
        }

    }
}
