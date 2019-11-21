package com.example.bite4byte.Messaging;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MsgApplication extends Application {
    private static Socket mSocket = null;

    public static Socket getInstance() {
        if (mSocket == null) {
            try {
                mSocket = IO.socket("http://10.0.2.2:3000");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }
}
