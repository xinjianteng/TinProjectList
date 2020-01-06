package com.tin.projectlist.app.library.reader.parser.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

/**
 *
 * 类名： .java<br>
 * 描述： CSS解析器 <br>
 * 创建者： yangn<br>
 * 创建日期：2013-5-15<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class CSSParser {

    public CSSParser(Reader reader) throws IOException {
        this.parse(reader);
    }

    private StringBuilder buffer = new StringBuilder();
    private ArrayList<StyleBlock> styles = new ArrayList<StyleBlock>();
    private StyleBlock entry = null;

    public void parse(InputStream in) {
    }


    /***
     * 执行解析
     * @param reader  css字符流
     * @throws IOException
     */
    public void parse(Reader reader) throws IOException {
        if (null == reader) {
            return;
        }

        if (null == styles) {
            styles = new ArrayList<StyleBlock>();
        } else {
            styles.clear();
        }

        int offset = -1, flag = -1;

        String key = null;

        while ((offset = reader.read()) != -1) {
            switch (offset) {
                default :
                    buffer.append((char) offset);
                    break;
                case 0x7b :// {
                    entry = new StyleBlock();
                    flag = buffer.lastIndexOf("*/");
                    if (flag == -1) {
                        entry.Name = buffer.toString().trim();
                    } else {
                        entry.Name = buffer.substring(flag + 2).toString().trim();
                    }
                    // System.out.println(entry.Name.charAt(0)+""+(int)entry.Name.charAt(0));
                    flag = -1;
                    clear();
                    break;
                case 0x7d :// }
                    if (null != entry) {
                        styles.add(entry);
                    }
                    clear();
                    break;
                case 0x3a :// :
                    key = buffer.toString().trim();
                    clear();
                    break;
                case 0x3b :// ;
                    if (null != key) {
                        if (null == entry) {
                            entry = new StyleBlock();
                        }
                        entry.Info.put(key, buffer.toString().trim());
                        key = null;
                        clear();
                    }
                    break;
            }
        }

        // 最后一个属性结束没有分号的情况
        if (null != key && buffer.length() != 0) {
            if (null == entry) {
                entry = new StyleBlock();
            }
            entry.Info.put(key, buffer.toString().trim());
            key = null;
            clear();
        }

        reader.close();
        reader = null;

    }

    private void clear() {
        buffer.delete(0, buffer.length());
    }

    public ArrayList<StyleBlock> getStyleBlocks() {
        return styles;
    }

    public StyleBlock getStyleBlock() {
        return this.entry;
    }

    /**
     * example 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-5-7<br>
     *
     * @param
     */
    /*
     * public static void maine(String arg[]) { String path =
     * "/home/android/yangn/testBook/乔布斯的魔力演讲（珍藏版）/OEBPS/Styles/stylesheet.css";
     * File file = new File(path); if (file.exists()) {
     * System.err.println("file exists"); } try { InputStreamReader reader = new
     * InputStreamReader(new FileInputStream(path), "utf-8"); CSSParser parser =
     * new CSSParser(reader); ArrayList<StyleEntry> styles =
     * parser.getStyleEntry(); if (null == styles || styles.size() == 0) {
     * System.err.println("list is null"); } else { for (StyleEntry entry :
     * styles) { System.out.println("name=" + entry.Name); if (entry.Info !=
     * null) { for (Iterator it = entry.Info.entrySet().iterator();
     * it.hasNext();) { Entry<String, String> item = (Entry<String, String>)
     * it.next(); System.out.println("key=" + item.getKey() + "val=" +
     * item.getValue()); } } } } } catch (FileNotFoundException e) { // TODO
     * Auto-generated catch block e.printStackTrace(); } catch (IOException e) {
     * // TODO Auto-generated catch block e.printStackTrace(); } }
     */
}
