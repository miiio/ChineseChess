package com.a510.chinesechess;

import android.app.Application;

import com.a510.chinesechess.Bean.PlayerBean;
import com.vilyever.socketclient.SocketClient;
/**
 * Created by Lao on 2018/2/9.
 */

public class MyApplication extends Application{
    public SocketClient getSocketClient() {
        return socketClient;
    }

    private SocketClient socketClient;

    public PlayerBean getPlayer() {
        return player;
    }

    public void setPlayer(PlayerBean player) {
        this.player = player;
    }

    private PlayerBean player;
    @Override
    public void onCreate() {
        super.onCreate();
        //SocketClient的初始化
        socketClient = new SocketClient("123.207.241.46",2345);
        socketClient.setConnectionTimeout(5000);
        socketClient.setCharsetName("UTF-8");
    }
}
