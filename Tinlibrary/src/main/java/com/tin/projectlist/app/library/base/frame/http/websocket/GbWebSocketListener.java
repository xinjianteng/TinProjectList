package com.tin.projectlist.app.library.base.frame.http.websocket;


import com.cliff.libs.util.LogUtils;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * 2019/12/2
 * author : chx
 * description :
 */
public class GbWebSocketListener extends WebSocketListener {

    private static final String TAG = GbWebSocketListener.class.getSimpleName();
    private ISocketListener mListener;
    private WebSocketApi mWebSocketApi;

    public GbWebSocketListener(WebSocketApi webSocketApi) {
        mWebSocketApi = webSocketApi;
    }

    public void setListener(ISocketListener listener) {
        mListener = listener;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        LogUtils.d(TAG, " onOpen ......");
        mWebSocketApi.setWebSocket(webSocket);
        if (mListener != null) {
            mListener.onOpen(webSocket, response);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        LogUtils.d(TAG, " onMessage  msg : " + text);
        if (mListener != null) {
            mListener.onMessage(text);
        }
        mWebSocketApi.close();
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        LogUtils.d(TAG, " onMessage  bytes.toString() : " + bytes.toString());
        if (mListener != null) {
            mListener.onMessage(bytes.toString());
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        LogUtils.d(TAG, " onClosing  code : " + code + "    reason :" + reason);
        if (webSocket != null) {
            mWebSocketApi.close();
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        LogUtils.d(TAG, " onClosed  code : " + code + "   reason : " + reason);
        if(mWebSocketApi != null){
            mWebSocketApi.removeListener(this);
        }

    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        LogUtils.d(TAG, " onFailure  t : " + t.getMessage());
        if (mListener != null) {
            mListener.onFailure(t);
        }
        if (mWebSocketApi != null) {
            mWebSocketApi.removeListener(this);
        }
    }
}
