package com.example.nettytest.meibeike.meiphoto.common.clientconnect.msgmgr;


import com.example.nettytest.meibeike.meiphoto.common.protocol.bmodel.IEntity;

public interface Observer {
  /**
   * 更新接口
   * 
   * @param state
   *          更新的状态
   */
  public void update(IEntity state);
}
