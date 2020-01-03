package com.tin.projectlist.app.library.reader.parser.common.util;
/**
 * 通用回调接口
 * @author yangn
 *
 * @param <T> 泛型值
 */
public interface IFunction<Result> {

    /**
     *
     * 功能描述： 回调函数<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-13<br>
     * @param result 泛型值
     */
    public void callback(final Result result);
}


