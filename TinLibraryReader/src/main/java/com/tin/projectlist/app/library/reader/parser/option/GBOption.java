package com.core.option;

import com.core.config.GBConfig;

/**
 * 自定义Option父类<br>
 * 向子类提供三个个方法，
 * 将Option信息保存到本地
 * （具体保存到哪里根据所调用的GBConfig实现类来确定）
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public abstract class GBOption {
    public static final String PLATFORM_GROUP = "PlatformOptions";

    private final String myGroup;// 组名
    private final String myOptionName;// 项目名
    protected boolean myIsSynchronized;

    /**
     * 构造方法
     *
     * @param group 组名
     * @param optionName 项目名
     */
    protected GBOption(String group, String optionName) {
        myGroup = group.intern();
        myOptionName = optionName.intern();
        myIsSynchronized = false;
    }

    /**
     * 获取当前option的值
     *
     * @param defaultValue
     * @return
     * @author fuchen
     * @date 2013-4-10
     */
    protected final String getConfigValue(String defaultValue) {
        GBConfig config = GBConfig.Instance();
        return (config != null) ? config.getValue(myGroup, myOptionName, defaultValue) : defaultValue;
    }

    /**
     * 设置option的值
     *
     * @param value
     * @author fuchen
     * @date 2013-4-10
     */
    protected final void setConfigValue(String value) {
        GBConfig config = GBConfig.Instance();
        if (config != null) {
            config.setValue(myGroup, myOptionName, value);
        }
    }

    /**
     * 重置option的值
     *
     * @author fuchen
     * @date 2013-4-10
     */
    protected final void unsetConfigValue() {
        GBConfig config = GBConfig.Instance();
        if (config != null) {
            config.unsetValue(myGroup, myOptionName);
        }
    }
}
