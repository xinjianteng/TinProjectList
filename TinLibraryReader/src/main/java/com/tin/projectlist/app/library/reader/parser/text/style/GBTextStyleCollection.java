package com.tin.projectlist.app.library.reader.parser.text.style;

import com.tin.projectlist.app.library.reader.parser.file.GBResourceFile;
import com.tin.projectlist.app.library.reader.parser.object.GBBoolean3;
import com.tin.projectlist.app.library.reader.parser.option.GBBooleanOption;
import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextAlignmentType;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;
import com.tin.projectlist.app.library.reader.parser.xml.GBXMLReaderAdapter;

/**
 * 类名： GBTextStyleCollection#ZLTextStyleCollection.java<br>
 * 描述： 内容渲染样式集合类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextStyleCollection {
    // 单一模式
    private static GBTextStyleCollection ourInstance = null;

    public static GBTextStyleCollection Instance() {
        if (ourInstance == null) {
            ourInstance = new GBTextStyleCollection();
        }
        return ourInstance;
    }

    public static void deleteInstance() {
        ourInstance = null;
    }

    private int myDefaultFontSize; // 默认字体大小
    private GBTextBaseStyle myBaseStyle; // 默认基本样式
    private final GBTextDecorationStyleOption[] myDecorationMap = new GBTextDecorationStyleOption[256]; // 样式配置数组

    public final GBBooleanOption UseCSSTextAlignmentOption = new GBBooleanOption("Style", "css:textAlignment", true);
    public final GBBooleanOption UseCSSFontSizeOption = new GBBooleanOption("Style", "css:fontSize", true);

    private GBTextStyleCollection() {
        new TextStyleReader(this).readQuietly(GBResourceFile.createResourceFile("default/styles.xml"));
    }

    public int getDefaultFontSize() {
        return myDefaultFontSize;
    }

    public GBTextBaseStyle getBaseStyle() {
        return myBaseStyle;
    }

    public GBTextDecorationStyleOption getDecoration(byte kind) {
        return myDecorationMap[kind & 0xFF];
    }
    /**
     * 类名： TextStyleReader<br>
     * 描述： 默认样式配置文件解析类<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-22<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    private static class TextStyleReader extends GBXMLReaderAdapter {
        private final int myDpi = GBLibrary.Instance().getDisplayDPI();
        private GBTextStyleCollection myCollection;

        public boolean dontCacheAttributeValues() {
            return true;
        }
        /**
         * 功能描述： 获取xml中属性配置的int值<br>
         * 创建者： jack<br>
         * 创建日期：2013-4-22<br>
         *
         * @param attributes 属性集合
         * @param name 属性名
         * @param defaultValue 默认值
         * @return
         */
        private int intValue(GBStringMap attributes, String name, int defaultValue) {
            int i = defaultValue;
            String value = attributes.getValue(name);
            if (value != null) {
                if (value.startsWith("dpi*")) {
                    try {
                        final float coe = Float.parseFloat(value.substring(4));
                        i = (int) (coe * myDpi + .5f);
                    } catch (NumberFormatException e) {
                    }
                } else {
                    try {
                        i = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return i;
        }

        private static boolean booleanValue(GBStringMap attributes, String name) {
            return "true".equals(attributes.getValue(name));
        }

        private static GBBoolean3 b3Value(GBStringMap attributes, String name) {
            return GBBoolean3.getByName(attributes.getValue(name));
        }

        public TextStyleReader(GBTextStyleCollection collection) {
            myCollection = collection;
        }

        public boolean startElementHandler(String tag, GBStringMap attributes) {
            final String BASE = "base";
            final String STYLE = "style";
            // 获取基本样式使用的字体和大小
            if (BASE.equals(tag)) {
                myCollection.myDefaultFontSize = intValue(attributes, "fontSize", 0);
                myCollection.myBaseStyle = new GBTextBaseStyle(attributes.getValue("family"),
                        myCollection.myDefaultFontSize);
            } else if (STYLE.equals(tag)) {
                String idString = attributes.getValue("id");
                String name = attributes.getValue("name");
                if ((idString != null) && (name != null)) {
                    byte id = Byte.parseByte(idString);
                    GBTextDecorationStyleOption decoration;

                    final String fontFamily = attributes.getValue("family");
                    final int fontSizeDelta = intValue(attributes, "fontSizeDelta", 0);
                    final GBBoolean3 bold = b3Value(attributes, "bold");
                    final GBBoolean3 italic = b3Value(attributes, "italic");
                    final GBBoolean3 underline = b3Value(attributes, "underline");
                    final GBBoolean3 strikeThrough = b3Value(attributes, "strikeThrough");
                    final int verticalShift = intValue(attributes, "vShift", 0);
                    final GBBoolean3 allowHyphenations = b3Value(attributes, "allowHyphenations");

                    if (booleanValue(attributes, "partial")) {
                        decoration = new GBTextDecorationStyleOption(name, fontFamily, fontSizeDelta, bold, italic,
                                underline, strikeThrough, verticalShift, allowHyphenations);
                    } else {
                        int spaceBefore = intValue(attributes, "spaceBefore", 0);
                        int spaceAfter = intValue(attributes, "spaceAfter", 0);
                        int leftIndent = intValue(attributes, "leftIndent", 0);
                        int rightIndent = intValue(attributes, "rightIndent", 0);
                        int firstLineIndentDelta = intValue(attributes, "firstLineIndentDelta", 0);

                        byte alignment = GBTextAlignmentType.ALIGN_UNDEFINED;
                        String alignmentString = attributes.getValue("alignment");
                        if (alignmentString != null) {
                            if (alignmentString.equals("left")) {
                                alignment = GBTextAlignmentType.ALIGN_LEFT;
                            } else if (alignmentString.equals("right")) {
                                alignment = GBTextAlignmentType.ALIGN_RIGHT;
                            } else if (alignmentString.equals("center")) {
                                alignment = GBTextAlignmentType.ALIGN_CENTER;
                            } else if (alignmentString.equals("justify")) {
                                alignment = GBTextAlignmentType.ALIGN_JUSTIFY;
                            }
                        }
                        final int lineSpacePercent = intValue(attributes, "lineSpacingPercent", -1);

                        decoration = new GBTextFullDecorationStyleOption(name, fontFamily, fontSizeDelta, bold, italic,
                                underline, strikeThrough, spaceBefore, spaceAfter, leftIndent, rightIndent,
                                firstLineIndentDelta, verticalShift, alignment, lineSpacePercent, allowHyphenations);
                    }

                    myCollection.myDecorationMap[id & 0xFF] = decoration;
                }
            }
            return false;
        }
    }
}
