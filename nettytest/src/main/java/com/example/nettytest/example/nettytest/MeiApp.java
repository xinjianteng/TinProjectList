package com.example.nettytest.example.nettytest;


import android.app.Application;
import android.content.Context;

import com.example.nettytest.meibeike.meiphoto.common.clientconnect.ClientConnectFactory;

public class MeiApp extends Application{
  
  public static Context mContext;
  
  @Override
  public void onCreate() {
    super.onCreate();
    mContext = this;
    
    ClientConnectFactory.getInstance().init(mContext);
  }
  
  
}
