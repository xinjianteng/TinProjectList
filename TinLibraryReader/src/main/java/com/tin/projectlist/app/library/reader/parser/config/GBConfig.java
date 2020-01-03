package com.tin.projectlist.app.library.reader.parser.config;

import java.util.List;
/**
 * 配置信息<br>
 *有groupName， name， value三个字段<br>
 *
 *groupName和name两个字段合起来构成主键<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-8<br>
 */
public abstract class GBConfig {
    public static GBConfig Instance() {
        return ourInstance;
    }

    private static GBConfig ourInstance;

    protected GBConfig() {
        ourInstance = this;
    }

    /**
     * 查询所有的group
     */
    public abstract List<String> listGroups();

    /**
     * 查询名字列表
     */
    public abstract List<String> listNames(String group);


    /**
     * 根据组名，键名，查询值
     */
    public abstract String getValue(String group, String name, String defaultValue);

    /**
     * 设置值
     */
    public abstract void setValue(String group, String name, String value);

    /**
     * 清空值
     */
    public abstract void unsetValue(String group, String name);


    /**
     * 删除组
     */
    public abstract void removeGroup(String name);
}
