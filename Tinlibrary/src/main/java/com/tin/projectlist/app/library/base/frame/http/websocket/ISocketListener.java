package com.tin.projectlist.app.library.base.frame.http.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * 2020/1/15
 * author : chx
 * description :
 */
public interface ISocketListener {
    void onOpen(WebSocket webSocket, Response response);

    void onMessage(String text);

    void onFailure(Throwable throwable);
}
