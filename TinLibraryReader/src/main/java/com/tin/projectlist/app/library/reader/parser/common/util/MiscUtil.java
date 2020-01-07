package com.tin.projectlist.app.library.reader.parser.common.util;

import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursor;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextWordCursor;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextWord;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 一些JAVA没有直接提供的常用的工具方法
 *
 * @author fuchen
 * @date 2013-4-9
 */
public abstract class MiscUtil {

    /**
     * 判断两个对象是否相同
     *
     * @param o0
     * @param o1
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static <T> boolean equals(T o0, T o1) {
        return o0 == null ? o1 == null : o0.equals(o1);
    }

    /**
     * 判断字符串是否为null或者空字符串
     *
     * @param s
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static boolean isEmptyString(String s) {
        return s == null || "".equals(s);
    }

    /**
     * 获取传入对象的hashCode，如果该对象为null，返回0
     *
     * @param o
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    /**
     * 判断两个列表是否相同
     *
     * @param list1
     * @param list2
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static <T> boolean listsEquals(List<T> list1, List<T> list2) {
        if (list1 == null) {
            return list2 == null || list2.isEmpty();
        }
        if (list1.size() != list2.size()) {
            return false;
        }
        return list1.containsAll(list2);
    }

    /**
     * 判断两个map是否相同
     *
     * @param map1
     * @param map2
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static <KeyT, ValueT> boolean mapsEquals(Map<KeyT, ValueT> map1, Map<KeyT, ValueT> map2) {
        if (map1 == null) {
            return map2 == null || map2.isEmpty();
        }
        if (map1.size() != map2.size() || !map1.keySet().containsAll(map2.keySet())) {
            return false;
        }
        for (KeyT key : map1.keySet()) {
            final ValueT value1 = map1.get(key);
            final ValueT value2 = map2.get(key);
            if (!equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断text中是否有lowerCasePattern。无视大小写
     *
     * @param text
     * @param lowerCasePattern
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static boolean matchesIgnoreCase(String text, String lowerCasePattern) {
        return (text.length() >= lowerCasePattern.length()) && (text.toLowerCase().indexOf(lowerCasePattern) >= 0);
    }

    /**
     * 将list中的字符串用delimiter链接起来，并返回
     *
     * @param list
     * @param delimiter
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static String join(List<String> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String s : list) {
            if (first) {
                first = false;
            } else {
                builder.append(delimiter);
            }
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * 将字符串分割成list并返回，join的逆方法
     *
     * @param str
     * @param delimiter
     * @return
     * @author fuchen
     * @date 2013-4-9
     */
    public static List<String> split(String str, String delimiter) {
        if (str == null || "".equals(str)) {
            return Collections.emptyList();
        }
        return Arrays.asList(str.split(delimiter));
    }

    /**
     * 功能描述： 获取文件的所在的路径地址<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param file 要获取的文件对象
     * @return
     */
    public static String htmlDirectoryPrefix(GBFile file) {
        String shortName = file.getShortName();
        String path = file.getPath();
        return path.substring(0, path.length() - shortName.length());
    }

    /**
     * 功能描述：获取文件路径中：号后的真实地址<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param fullPath 要处理的特殊路径
     * @return
     */
    public static String archiveEntryName(String fullPath) {
        final int index = fullPath.lastIndexOf(':');
        return (index >= 2) ? fullPath.substring(index + 1) : fullPath;
    }

    /**
     * 功能描述： 检查字符是否字母数字<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param ch 要检查的字符
     * @return
     */
    private static boolean isHexDigit(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    /**
     * 功能描述： 解码epub中存在特殊字符的html文件地址的转码串<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param name 要处理的文件转码串
     * @return
     */
    public static String decodeHtmlReference(String name) {
        if (name == null) {
            return null;
        }

        int index = 0;
        while (true) {
            index = name.indexOf('%', index);
            if (index == -1 || index >= name.length() - 2) {
                break;
            }
            if (isHexDigit(name.charAt(index + 1)) && isHexDigit(name.charAt(index + 2))) {
                char c = 0;
                try {
                    c = (char) Integer.decode("0x" + name.substring(index + 1, index + 3)).intValue();
                } catch (NumberFormatException e) {
                }
                name = name.substring(0, index) + c + name.substring(index + 3);
            }
            index = index + 1;
        }
        return name;
    }

    /***
     * 是否是简帛制作的TXT文件
     *
     * @param txtFile
     * @return 是返回true否则返回false
     * @throws IOException
     */
    public static boolean isTxtMadeInGeeboo(File txtFile) throws IOException {
        if (null == txtFile) {
            return false;
        }
        if (!txtFile.getName().endsWith("txt") && !txtFile.getName().endsWith("TXT")) {
            return false;
        }
        try {
            new ZipFile(txtFile);
            return true;
        } catch (ZipException e) {
            return false;
        }
    }

    /**
     *
     * 功能描述： 章节字符位置转换为内部位置格式<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-23<br>
     *
     * @param position chpInWordNum具有有效值的位置描述
     */
    public static int[] convertChpInWordNumToInternalPosition(GBTextModel model, GBTextPosition position) {
        if (position.getChpInWordNum() == 0) {
            return new int[]{0, 0, 0};
        }
        int totalChpInParagraphNum = model.getParagraphsNumber(position.getChpFileIndex());
        // long[] startAndEndTextOffset =
        // writableMode.getChpTextSizeByChpFileIndex(position.getChpFileIndex());

        int paragraphIndex = 0, elementIndex = 0, charIndex = 0;

        // long wordSize = startAndEndTextOffset[0];
        int wordSize = 0;
        GBTextWord word = null;

        boolean flag = true;
        do {
            GBTextParagraphCursor pc = GBTextParagraphCursor.cursor(model, position.getChpFileIndex(), paragraphIndex);
            if (pc.wordNum < (position.getChpInWordNum() - wordSize)) {
                wordSize += pc.wordNum;
            } else {
                for (elementIndex = 0; elementIndex < pc.getParagraphLength(); elementIndex++) {
                    if (pc.getElement(elementIndex) instanceof GBTextWord) {
                        word = (GBTextWord) pc.getElement(elementIndex);
                        wordSize += word.Length;
                        if (position.getChpInWordNum() <= wordSize) {
                            charIndex = word.Length - ((int) (wordSize - position.getChpInWordNum()));
                            charIndex -= 1;
                            flag = false;
                            break;
                        }
                    }
                }
            }

            if (flag) {
                paragraphIndex++;
            }

        } while (flag && paragraphIndex < totalChpInParagraphNum);
        return new int[]{paragraphIndex, elementIndex, charIndex};

    }

    /**
     *
     * 功能描述： 内部位置格式转换为章节位置数<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-24<br>
     *
     * @param
     */
    public static void convertInternalPositionToChpInWordNum(GBTextModel model, GBTextWordCursor startCursor) {
        if (startCursor.isNull()) {
            return;
        }
        int paragraphIndex = 0, elementIndex = 0, charIndex = 0, wordSize = 0;
        int totalChpInParagraphNum = startCursor.getParagraphIndex();
        boolean flag = true;
        GBTextWord word = null;

        do {

            GBTextParagraphCursor pc = GBTextParagraphCursor.cursor(model, startCursor.getChpFileIndex(),
                    paragraphIndex);
            if (startCursor.getParagraphIndex() < pc.Index) {
                wordSize += pc.wordNum;
            } else {
                for (elementIndex = 0; elementIndex < pc.getParagraphLength(); elementIndex++) {
                    if (pc.getElement(elementIndex) instanceof GBTextWord) {

                        word = (GBTextWord) pc.getElement(elementIndex);

                        if (startCursor.getChpFileIndex() == pc.chpFileIndex
                                && startCursor.getParagraphIndex() == pc.Index
                                && (startCursor.getElementIndex() == elementIndex)) {
                            wordSize += startCursor.getCharIndex();
                            startCursor.setChpInWordNum(wordSize);
                            flag = false;
                            break;
                        } else {
                            wordSize += word.Length;
                        }
                    }
                }
            }
            if (flag) {
                paragraphIndex++;
            }
        } while (flag && paragraphIndex <= totalChpInParagraphNum);

    }

}
