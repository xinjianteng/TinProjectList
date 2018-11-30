package com.tin.library.mvp;

public interface IBaseVIew<T> {

    /***
     * 设置
     * @param t
     */
    void setPresenter(T t);

    /***
     * 绑定
     * @return
     */
    T bindPresenter();



}
