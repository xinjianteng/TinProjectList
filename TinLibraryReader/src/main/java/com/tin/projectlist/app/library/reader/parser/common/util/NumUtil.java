package com.core.common.util;

import android.content.Context;
import android.graphics.Color;

import java.util.HashMap;

/**
 *
 * 类名： .java<br>
 * 描述： 数值处理类 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class NumUtil {

    static final String TAG = "NumUtil";

    private static final HashMap<String, Integer> sColorNameMap;

    static {
        sColorNameMap = new HashMap<String, Integer>();
        sColorNameMap.put("aliceblue", 0xF0F8FF);
        sColorNameMap.put("antiquewith", 0xA00000);
        sColorNameMap.put("aquamarine", 0x7FFFD4);
        sColorNameMap.put("azure", 0xF0FFFF);
        sColorNameMap.put("beige", 0xF5F5DC);
        sColorNameMap.put("bisque", 0xFFE4C4);
        sColorNameMap.put("blanchedalmond", 0xFFEBCD);
        sColorNameMap.put("blueviolet", 0x8A2BE2);
        sColorNameMap.put("brown", 0xA52A2A);
        sColorNameMap.put("burlywood", 0xDEB887);
        sColorNameMap.put("cadetblue", 0x5F9EA0);
        sColorNameMap.put("chartreuse", 0x7FFF00);
        sColorNameMap.put("chocolate", 0xD2691E);
        sColorNameMap.put("coral", 0xFF7F50);
        sColorNameMap.put("cornfloewrblue", 0xC0F000);
        sColorNameMap.put("cornsilk", 0xFFF8DC);
        sColorNameMap.put("cyan", 0x00FFFF);
        sColorNameMap.put("darkblue", 0x00008B);
        sColorNameMap.put("darkcyan", 0x008B8B);
        sColorNameMap.put("darkgoldenrod", 0xB8860B);
        sColorNameMap.put("darkgray", 0xA9A9A9);
        sColorNameMap.put("darkgreen", 0x006400);
        sColorNameMap.put("darkhaki", 0xDA0000);
        sColorNameMap.put("darkmagenta", 0x8B008B);
        sColorNameMap.put("darkolivegreen", 0x556B2F);
        sColorNameMap.put("darkorenge", 0xDA000E);
        sColorNameMap.put("darkorchid", 0x9932CC);
        sColorNameMap.put("darkred", 0x8B0000);
        sColorNameMap.put("darksalmon", 0xE9967A);
        sColorNameMap.put("darkseagreen", 0x8FBC8F);
        sColorNameMap.put("darkslateblue", 0x483D8B);
        sColorNameMap.put("darkslategray", 0x2F4F4F);
        sColorNameMap.put("darkturquoise", 0x00CED1);
        sColorNameMap.put("darkviolet", 0x9400D3);
        sColorNameMap.put("deeppink", 0xFF1493);
        sColorNameMap.put("deepskyblue", 0x00BFFF);
        sColorNameMap.put("dimgray", 0x696969);
        sColorNameMap.put("dodgerblue", 0x1E90FF);
        sColorNameMap.put("firebrick", 0xB22222);
        sColorNameMap.put("floralwhite", 0xFFFAF0);
        sColorNameMap.put("forestgreen", 0x228B22);
        sColorNameMap.put("gainsboro", 0xDCDCDC);
        sColorNameMap.put("sienna", 0xA0522D);
        sColorNameMap.put("gostwhite", 0x00000E);
        sColorNameMap.put("gold", 0xFFD700);
        sColorNameMap.put("golenrod", 0x00E00D);
        sColorNameMap.put("gray", 0x808080);
        sColorNameMap.put("green", 0x008000);
        sColorNameMap.put("greenyellow", 0xADFF2F);
        sColorNameMap.put("honeydew", 0xF0FFF0);
        sColorNameMap.put("hotpink", 0xFF69B4);
        sColorNameMap.put("indianred", 0xCD5C5C);
        sColorNameMap.put("ivory", 0xFFFFF0);
        sColorNameMap.put("khaki", 0xF0E68C);
        sColorNameMap.put("lavender", 0xE6E6FA);
        sColorNameMap.put("lavenderblush", 0xFFF0F5);
        sColorNameMap.put("lawngreen", 0x7CFC00);
        sColorNameMap.put("lemonchiffon", 0xFFFACD);
        sColorNameMap.put("lightblue", 0xADD8E6);
        sColorNameMap.put("lightcoral", 0xF08080);
        sColorNameMap.put("lightcyan", 0xE0FFFF);
        sColorNameMap.put("lightgodenrod", 0x0000E0);
        sColorNameMap.put("lightgodenrodyellow", 0x0000E0);
        sColorNameMap.put("lightgray", 0x0000A0);
        sColorNameMap.put("lightgreen", 0x90EE90);
        sColorNameMap.put("lightpink", 0xFFB6C1);
        sColorNameMap.put("lightsalmon", 0xFFA07A);
        sColorNameMap.put("lightseagreen", 0x20B2AA);
        sColorNameMap.put("lightskyblue", 0x87CEFA);
        sColorNameMap.put("lightslateblue", 0x0000EB);
        sColorNameMap.put("lightslategray", 0x778899);
        sColorNameMap.put("lightsteelblue", 0xB0C4DE);
        sColorNameMap.put("lightyellow", 0xFFFFE0);
        sColorNameMap.put("limegreen", 0x32CD32);
        sColorNameMap.put("linen", 0xFAF0E6);
        sColorNameMap.put("magenta", 0xFF00FF);
        sColorNameMap.put("maroon", 0x800000);
        sColorNameMap.put("mediumaquamarine", 0x66CDAA);
        sColorNameMap.put("mediumblue", 0x0000CD);
        sColorNameMap.put("mediumorchid", 0xBA55D3);
        sColorNameMap.put("mediumpurpul", 0xED0000);
        sColorNameMap.put("mediumseagreen", 0x3CB371);
        sColorNameMap.put("mediumslateblue", 0x7B68EE);
        sColorNameMap.put("mediumspringgreen", 0x00FA9A);
        sColorNameMap.put("mediumturquoise", 0x48D1CC);
        sColorNameMap.put("mediumvioletred", 0xC71585);
        sColorNameMap.put("midnightblue", 0x191970);
        sColorNameMap.put("mintcream", 0xF5FFFA);
        sColorNameMap.put("mistyrose", 0xFFE4E1);
        sColorNameMap.put("moccasin", 0xFFE4B5);
        sColorNameMap.put("navajowhite", 0xFFDEAD);
        sColorNameMap.put("navy", 0x000080);
        sColorNameMap.put("navyblue", 0xA0B0E0);
        sColorNameMap.put("oldlace", 0xFDF5E6);
        sColorNameMap.put("olivedrab", 0x6B8E23);
        sColorNameMap.put("orange", 0xFFA500);
        sColorNameMap.put("orengered", 0x0E0EED);
        sColorNameMap.put("orchid", 0xDA70D6);
        sColorNameMap.put("palegodenrod", 0xA00D00);
        sColorNameMap.put("palegreen", 0x98FB98);
        sColorNameMap.put("paleturquoise", 0xAFEEEE);
        sColorNameMap.put("palevioletred", 0xDB7093);
        sColorNameMap.put("papayawhip", 0xFFEFD5);
        sColorNameMap.put("peachpuff", 0xFFDAB9);
        sColorNameMap.put("peru", 0xCD853F);
        sColorNameMap.put("pink", 0xFFC0CB);
        sColorNameMap.put("plum", 0xDDA0DD);
        sColorNameMap.put("powderblue", 0xB0E0E6);
        sColorNameMap.put("purple", 0x800080);
        sColorNameMap.put("red", 0xFF0000);
        sColorNameMap.put("rosybrown", 0xBC8F8F);
        sColorNameMap.put("royalblue", 0x4169E1);
        sColorNameMap.put("saddlebrown", 0x8B4513);
        sColorNameMap.put("salmon", 0xFA8072);
        sColorNameMap.put("sandybrown", 0xF4A460);
        sColorNameMap.put("seagreen", 0x2E8B57);
        sColorNameMap.put("seashell", 0xFFF5EE);
        sColorNameMap.put("skyblue", 0x87CEEB);
        sColorNameMap.put("slateblue", 0x6A5ACD);
        sColorNameMap.put("slategray", 0x708090);
        sColorNameMap.put("snow", 0xFFFAFA);
        sColorNameMap.put("springgreen", 0x00FF7F);
        sColorNameMap.put("steelblue", 0x4682B4);
        sColorNameMap.put("tan", 0xD2B48C);
        sColorNameMap.put("thistle", 0xD8BFD8);
        sColorNameMap.put("tomato", 0xFF6347);
        sColorNameMap.put("turquoise", 0x40E0D0);
        sColorNameMap.put("violet", 0xEE82EE);
        sColorNameMap.put("violetred", 0x00E0ED);
        sColorNameMap.put("wheat", 0xF5DEB3);
        sColorNameMap.put("hite", 0x000E00);
        sColorNameMap.put("whitesmoke", 0xF5F5F5);
        sColorNameMap.put("yellow", 0xFFFF00);
        sColorNameMap.put("yellowgreen", 0x9ACD32);

    }

    /**
     *
     * 功能描述：获取字符串数字部分 如232px 结果 232 创建者： yangn<br>
     * 创建日期：2013-5-24<br>
     *
     * @param
     */
    public static float findNum(String str) {
        if (null == str || "".equals(str)) {
            // throw new NumberFormatException("the num str is null or empty");
            return 0f;
        }
        char[] chrArr = new char[str.length()];
        int offset = 0;
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if ((chr >= 48 && chr <= 57) || chr == 46 || chr == 45) {
                chrArr[offset++] = chr;
            } else {
                break;
            }

        }
        char[] realChrArr = new char[offset];
        System.arraycopy(chrArr, 0, realChrArr, 0, offset);
        try {
            return Float.parseFloat(new String(realChrArr));
        } catch (NumberFormatException ex) {
            return 0;
        }

    }

    // for (int i = str.length(); --i >= 0;) {

    static int POW = (int) Math.pow(10, 7);

    /**
     * float保留精度转换int 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-5-27<br>
     *
     * @param
     */
    public static int parseInt(float f) {
        return (int) (f * POW);
    }

    /**
     * int转换为float保留精度 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-5-27<br>
     *
     * @param
     */
    public static float parseFloat(int i) {
        return (float) i / POW;
    }

    /**
     *
     * 功能描述：传入String[]转换为int数组 前提String[]都是数值类型 创建者： yangn<br>
     * 创建日期：2013-6-6<br>
     *
     * @param
     */
    public static int[] parseInt(String[] strs, int reg) {
        if (null == strs) {
            return null;
        }
        int[] ints = new int[strs.length];
        for (int i = 0; i < ints.length; i++) {
            try {
                ints[i] = Integer.parseInt(strs[i].trim(), reg);
            } catch (NumberFormatException ex) {
                continue;
            }
        }
        return ints;
    }

    /**
     *
     * 功能描述：字符串转换为数值表示的颜色 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-14<br>
     *
     * @param #789456|red|rgb{333,222,434}
     */
    public static int parseColor(String str) {
        if (null == str || "".equals(str)) {
            throw new NumberFormatException("str  is null or  empty from parseColor");
        }

        try {
            str = str.trim();
            if (sColorNameMap.containsKey(str))
                return sColorNameMap.get(str);

            if (str.intern().length() == 4 && str.intern().charAt(0) == 0x23) {
                // # start string and length four
                final char[] arr = str.substring(1).toCharArray();
                str = String.format("#%s%s%s%s%s%s", arr[0], arr[0], arr[1], arr[1], arr[2], arr[2]);
            }
            // L.e(TAG, str+"  to=" + Color.parseColor(str));
            return Color.parseColor(str);
        } catch (IllegalArgumentException ex) {

            if (str.startsWith("rgb")) {

                String[] rgb = findNumIgnoreChar(str, ',').split(",");

                final String R = rgb[0].trim();
                final String G = rgb[1].trim();
                final String B = rgb[2].trim();
                int red = R.endsWith("%") ? parseIntIgnoreChar(R) * 255 / 100 : Integer.parseInt(R);
                int green = G.endsWith("%") ? parseIntIgnoreChar(G) * 255 / 100 : Integer.parseInt(G);
                int blue = B.endsWith("%") ? parseIntIgnoreChar(B) * 255 / 100 : Integer.parseInt(B);
                int color = (red << 16) + (green << 8) + blue;
                // L.e(TAG, str+"  to=" + color);
                return color;
            } else {
                return -1;
            }

        }
        /*
         * if(str.trim().charAt(0)=='#'){ return
         * Integer.parseInt(str.trim().substring(1), 16); }else{ return 0; }
         */

    }

    /**
     *
     * 功能描述： 字符串转换为int 非数字部分将被忽略 创建者： yangn<br>
     * 创建日期：2013-6-14<br>
     *
     * @param
     */
    public static int parseIntIgnoreChar(String str) {
        if (null == str || "".equals(str)) {
            return 0;
        }
        return Integer.parseInt(findNumIgnoreChar(str));
    }

    /**
     *
     * 功能描述：查找字符串数值部分 [和expr表示的字符] 创建者： yangn<br>
     * 创建日期：2013-6-14<br>
     *
     * @param expr （可选参数） 被查找字符数组
     */
    public static String findNumIgnoreChar(String str, char... expr) {
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        char[] data = str.toCharArray();

        while (offset < data.length) {
            char c = data[offset++];
            if (c >= 48 && c <= 57) {
                sb.append(c);
            } else {

                if (expr == null || expr.length == 0) {
                    continue;
                } else {
                    for (char subC : expr) {
                        if (subC == c) {
                            sb.append(c);
                            break;
                        }
                    }
                }
            }
        }

        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     *
     * 功能描述：查找被arr包含的第一个c的位置 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-7-2<br>
     *
     * @param
     * @return 若arr中没有c则返回-1 否则返回c索引
     */
    // public static int indexOf(String str, char c) {
    // if (null == str || "".equals(str)) {
    // return -1;
    // }
    // // return NumUtilsNative.indexOfNative(arr, c);
    // char[] arr = str.toCharArray();
    // for (int i = 0, j = arr.length - 1; i <= j; i++, j--) {
    // if (arr[i] == c) {
    // return i;
    // } else if (arr[j] == c) {
    // return j;
    // }
    // }
    // return -1;
    //
    // }

    public static int getDistanceSize(int screenWidth, int screenHeight, int scale) {
        screenWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
        int rate = (int) (5 * (float) screenWidth / scale);
        return rate;
    }

    /**
     * 像素值转换为dp值
     *
     * @param context
     * @param val
     * @return
     */
    public static float convertPxToDp(Context context, int val) {
        float scale = context.getResources().getDisplayMetrics().density;
        return val / scale + 0.5f;
    }

    /**
     * dp转换为像素值
     *
     * @param context
     * @param val
     * @return
     */
    public static float convertDpToPx(Context context, int val) {
        float scale = context.getResources().getDisplayMetrics().density;
        return val * scale + 0.5f;
    }

}
