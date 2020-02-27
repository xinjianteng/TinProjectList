package com.tin.projectlist.app.library.base.utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @package : com.cliff.libs.util
 * @description :
 * Created by chenhx on 2018/4/2 17:58.
 */

public class ListUtil {

    public static boolean isListNull(List list) {
        return list == null || list.size() <= 0;
    }


    public static int size(List list) {
        if (list == null) {
            return 0;
        }
        return list.size();
    }


    public static boolean isMapNull(Map map) {
        return map == null || map.size() <= 0;
    }


    /***
     * map 转 list
     * @param map
     * @return
     */
    public static List transToList(Map map) {
        List list = Collections.emptyList();
        Set<Map.Entry> set = map.entrySet();
        for (Map.Entry entry : set) {
            list.add(entry.getValue());
        }
        return list;
    }


    /***
     * 去重
     * @param list
     */
    public static void removeDuplicate(List list) {
        LinkedHashSet set = new LinkedHashSet(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }
}
