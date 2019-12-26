package com.tin.projectlist.app.library.reader.utils;

/**
 *
 * 类名： .java<br>
 * 描述： 通用反射接口<br>
 * 创建者： yangn<br>
 * 创建日期：2014-1-13<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 * @param <Result>  泛型值
 */
public interface IGiveback<Result> {

    /**
     *
     * 功能描述： 回复（返还）函数<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-13<br>
     * @return 泛型值
     */
    public Result get();

}
