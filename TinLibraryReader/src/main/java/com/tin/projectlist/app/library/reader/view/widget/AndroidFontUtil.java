package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Typeface;

import com.core.common.util.GBTTFInfoDetector;
import com.core.file.GBPaths;
import com.core.log.L;
import com.geeboo.read.model.book.TypeFace;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 类名： AndroidFontUtil.java<br>
 * 描述： 字体支持类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class AndroidFontUtil {
    /**
     * 功能描述：获取本地字体列表<br>
     * 创建者： 周波<br>
     * 创建日期：2015-2-5下午2:27:27<br>
     * 版本： V0.1 <br>
     * 修改者： <br>
     * 修改日期：<br>
     *
     * @return
     */
    public static List<TypeFace> getLocalFamiliesList() {
        List<TypeFace> localFamiliesList = new ArrayList<TypeFace>();
        // 添加android系统默认字体
        localFamiliesList.add(new TypeFace("Droid Sans", "", true));
        // familySet.add("Droid Serif");
        // familySet.add("Droid Mono");
        // 遍历本地字体
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(".")) {
                    return false;
                }
                final String lcName = name.toLowerCase();
                return lcName.endsWith(".ttc") || lcName.endsWith(".ttf");
            }
        };
        String fontName = null;
        File[] fileList = new File(GBPaths.getFontsPathOption().getValue()).listFiles(filter);
        if (fileList != null && fileList.length > 0) {
            for (File fontFile : fileList) {
                if (fontFile.getName().lastIndexOf(".") > 0) {
                    fontName = fontFile.getName().substring(0, fontFile.getName().lastIndexOf("."));
                } else {
                    fontName = fontFile.getName();
                }
                localFamiliesList.add(new TypeFace(fontName, fontFile.getName(), true));
            }
        }
        return localFamiliesList;
    }
    private static Map<String, File[]> ourFontMap;
    private static Set<File> ourFileSet;
    private static long ourTimeStamp;

    /**
     * 功能描述： 读取字体文件字体名称<br>
     * 创建者： jack<br>
     * 创建日期：2015-2-11<br>
     *
     * @param file 文件对象
     */
    public static String getRealFontName(File file) {
        for (String name : getFontMap(false).keySet()) {
            if (ourFontMap.get(name)[0] != null && ourFontMap.get(name)[0].equals(file))
                return name;
        }
        Map<String, File[]> map = getFontMap(false);
        String name = "";
        Set<String> names = map.keySet();
//        for (int i = 0; i < names.size(); i++) {
////            name = names[i];
//            if (ourFontMap.get(name)[0] != null && ourFontMap.get(name)[0].equals(file))
//        }
        // final GBTTFInfoDetector gbfd = new GBTTFInfoDetector();
        // try {
        // final GBTTFInfo info = gbfd.detectInfo(file);
        // return info.FamilyName;
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        return "serif";
    }
    /**
     *
     * @param forceReload 是否加载ttf文件
     * @returnkey:字体名称 val 字体文件路径
     */
    private static Map<String, File[]> getFontMap(boolean forceReload) {
        final long timeStamp = System.currentTimeMillis();
        if (forceReload && timeStamp < ourTimeStamp + 1000) {
            forceReload = false;
        }
        ourTimeStamp = timeStamp;
        if (ourFileSet == null || forceReload) {
            final HashSet<File> fileSet = new HashSet<File>();
            final FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.startsWith(".")) {
                        return false;
                    }
                    final String lcName = name.toLowerCase();
                    return lcName.endsWith(".ttc") || lcName.endsWith(".ttf");
                }
            };
            final File[] fileList = new File(GBPaths.getFontsPathOption().getValue()).listFiles(filter);
            if (fileList != null) {
                fileSet.addAll(Arrays.asList(fileList));
            }
            if (!fileSet.equals(ourFileSet)) {
                ourFileSet = fileSet;
                ourFontMap = new GBTTFInfoDetector().collectFonts(fileSet);
            }
        }
        return ourFontMap;
    }
    /*
     * 字体名称处理
     */
    public static String realFontFamilyName(String fontFamily) {
        for (String name : getFontMap(false).keySet()) {
            if (name.equalsIgnoreCase(fontFamily)) {
                return name;
            }
        }
        if ("serif".equalsIgnoreCase(fontFamily) || "droid serif".equalsIgnoreCase(fontFamily)) {
            return "serif";
        }
        if ("sans-serif".equalsIgnoreCase(fontFamily) || "sans serif".equalsIgnoreCase(fontFamily)
                || "droid sans".equalsIgnoreCase(fontFamily)) {
            return "sans-serif";
        }
        if ("monospace".equalsIgnoreCase(fontFamily) || "droid mono".equalsIgnoreCase(fontFamily)) {
            return "monospace";
        }
        return fontFamily;
    }

    private static final HashMap<String, Typeface[]> ourTypefaces = new HashMap<String, Typeface[]>();
    /**
     * 功能描述： 获取字体样式<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @param family 字体名
     * @param bold 是否加粗
     * @param italic 是否斜体
     * @return
     */
    static Typeface mTypeface;

    /**
     * 通过字体名称获取字体
     *
     * @param family
     * @param bold
     * @param italic
     * @return
     */
    public static Typeface typeface(String family, boolean bold, boolean italic) {
        // if (family.equals("default")) {
        // if (mTypeface == null)
        // mTypeface = Typeface.createFromAsset(((GBAndroidLibrary)
        // GBLibrary.Instance()).getActivity()
        // .getAssets(), "fonts/Default.ttf");
        // L.i("FontUtil", family+"--"+mTypeface.toString());
        // return mTypeface;
        // }
        // L.e("AndroidFontUtil", "family==" + family);
        family = realFontFamilyName(family);
        // L.e("AndroidFontUtil", "family  val==" + family);
        final int style = (bold ? Typeface.BOLD : 0) | (italic ? Typeface.ITALIC : 0);
        Typeface[] typefaces = ourTypefaces.get(family);
        if (typefaces == null) {
            typefaces = new Typeface[4];
            ourTypefaces.put(family, typefaces);
        }
        Typeface tf = typefaces[style];

        if (tf == null) {

            File[] files = getFontMap(false).get(family);
            L.e("android font util files=", "" + files);
            if (files != null) {
                try {
                    if (files[style] != null) {
                        tf = Typeface.createFromFile(files[style]);
                    } else {
                        for (int i = 0; i < 4; ++i) {
                            if (files[i] != null) {
                                tf = (typefaces[i] != null) ? typefaces[i] : Typeface.createFromFile(files[i]);
                                typefaces[i] = tf;
                                break;
                            }
                        }
                    }
                } catch (Throwable e) {
                }
            }
            if (tf == null) {
                tf = Typeface.create(family, style);
            }
            typefaces[style] = tf;
        }
        return tf;
    }
    /**
     * 功能描述： 清除字体缓存<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     */
    public static void clearFontCache() {
        ourTypefaces.clear();
        ourFileSet = null;
    }
}
