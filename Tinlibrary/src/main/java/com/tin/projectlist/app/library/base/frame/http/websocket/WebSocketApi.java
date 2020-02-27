package com.tin.projectlist.app.library.base.frame.http.websocket;

import com.cliff.libs.http.RetrofitUtil;
import com.cliff.libs.http.model.ApiHeaders;
import com.cliff.libs.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * 2019/12/2
 * author : chx
 * description :
 */
public class WebSocketApi {

    public static volatile WebSocketApi instance;

    private List<GbWebSocketListener> mGbWebSocketListenerList = new ArrayList<>();
    private WebSocket mWebSocket;

    public static WebSocketApi getInstance() {
        if (instance == null) {
            synchronized (WebSocketApi.class) {
                if (instance == null) {
                    instance = new WebSocketApi();
                }
            }
        }
        return instance;
    }


    public void connect(String url, String authorization, ISocketListener listener) {
        OkHttpClient okHttpClient = RetrofitUtil.getOkHttpClientBuilder().build();
        Request request = new Request.Builder().url(url).addHeader(ApiHeaders.AUTHORIZATION, authorization).build();
        GbWebSocketListener gbWebSocketListener = new GbWebSocketListener(this);
        gbWebSocketListener.setListener(listener);
        mGbWebSocketListenerList.add(gbWebSocketListener);
        okHttpClient.newWebSocket(request, gbWebSocketListener);
    }


    public void setWebSocket(WebSocket webSocket) {
        mWebSocket = webSocket;
    }

    public void sendMsg(String text) {
        if (mWebSocket != null) {
            mWebSocket.send(text);
        }
    }

    public void close() {
        if (mWebSocket != null) {
            mWebSocket.close(1000, null);
            mWebSocket = null;
        }
    }

    public void removeListener(GbWebSocketListener gbWebSocketListener) {
        if (!ListUtil.isListNull(mGbWebSocketListenerList)) {
            mGbWebSocketListenerList.remove(gbWebSocketListener);
        }

    }

    public void clear() {
        if (!ListUtil.isListNull(mGbWebSocketListenerList)) {
            mGbWebSocketListenerList.clear();
        }

    }
}
