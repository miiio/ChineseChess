package com.a510.chinesechess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.a510.chinesechess.Bean.PlayerBean;
import com.a510.chinesechess.Bean.RoomsBean;
import com.a510.chinesechess.Fragment.InputDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.SocketResponsePacket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chen on 2018/1/8.
 */

public class MenuActivity extends AppCompatActivity {

    private ImageView mSingleBtn, mOnlineBtn;
    private SocketClient socketClient;

    private void bindView() {
        mSingleBtn = (ImageView)findViewById(R.id.singlebtn);
        mOnlineBtn = (ImageView)findViewById(R.id.onlinebtn);

        mSingleBtn.setOnTouchListener(new TouchEvent());
        mOnlineBtn.setOnTouchListener(new TouchEvent());
    }

    private SocketClient.SocketDelegate connectionDelegate = new SocketClient.SocketDelegate() {
        @Override
        public void onConnected(SocketClient client) {
            Toast.makeText(MenuActivity.this,"连接服务器成功!",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(SocketClient client) {
            Toast.makeText(MenuActivity.this,"与服务器断开连接",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
            String json = responsePacket.getMessage();
            if(json == null){
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(json);
                //{"type":"connection_success","data":{"id":1,"name":"player1","status":"free","room_id":0,"ready":false,"game_color":0}}
                switch (jsonObject.getString("type")){
                    case "connection_success":
                        String dataJson = jsonObject.get("data").toString();
                        final PlayerBean player = new Gson().fromJson(dataJson, PlayerBean.class);
                        ((MyApplication)getApplication()).setPlayer(player);
                        new InputDialogFragment()
                                .setDefaultEditText(player.getName())
                                .setMsg("请设置你的名字")
                                .setOkBtnListener(new InputDialogFragment.InputDialogOnClickListener() {
                                    @Override
                                    public void onClick(String editText) {
                                        if(editText != player.getName()) {
                                            String data = "{\"mod\":\"Player\", \"act\":\"rename\", \"args\"" +
                                                    ":{\"player_id\":" + player.getId() + ",\"name_new\":\"" + editText + "\"}}";
                                            socketClient.sendString(data);
                                        }else{
                                            jumpToOnlineActivity();
                                        }
                                    }
                                })
                                .setCanelBtnListener(new InputDialogFragment.InputDialogOnClickListener() {
                                    @Override
                                    public void onClick(String editText) {
                                        socketClient.disconnect();
                                    }
                                })
                                .show(getSupportFragmentManager(),"input_name");
                        break;

                    case "rename_success":
                        jumpToOnlineActivity();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void jumpToOnlineActivity(){
        //去掉回调
        socketClient.removeSocketDelegate(connectionDelegate);
        Intent intent = new Intent(MenuActivity.this, OnlineActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        if(!socketClient.isDisconnected()){
            socketClient.disconnect();
        }
        socketClient.registerSocketDelegate(connectionDelegate);
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //绑定控件
        bindView();
        socketClient = ((MyApplication)getApplication()).getSocketClient();
        socketClient.registerSocketDelegate(connectionDelegate);

    }

    public class TouchEvent implements View.OnTouchListener {

        boolean isInRange(float x, float y) {
            float xx = px2dip(MenuActivity.this, x);
            float yy = px2dip(MenuActivity.this, y);
            float maxX = 75,maxY = 96, minX = 2, minY = 9;
            return xx <= maxX && yy <= maxY && xx >= minX && yy >= minY;
        }

        int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {


            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d("Range: ", "X = " + px2dip(MenuActivity.this, event.getX())
                        + " Y = " + px2dip(MenuActivity.this, event.getY()));
                if (isInRange(event.getX(), event.getY())) {
                    switch (v.getId()) {
                        case R.id.singlebtn : ((ImageView) v).
                                setImageResource(R.drawable.imgbtn_single_pre);break;
                        case R.id.onlinebtn : ((ImageView) v).
                                setImageResource(R.drawable.imgbtn_online_pre);break;
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isInRange(event.getX(), event.getY())) {
                    if (v.getId() == R.id.onlinebtn) {
                        socketClient.connect();
                    } else if (v.getId() == R.id.singlebtn) {
                        Intent intent = new Intent(MenuActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                }

                switch (v.getId()) {
                    case R.id.singlebtn : ((ImageView) v).
                            setImageResource(R.drawable.imgbtn_single);break;
                    case R.id.onlinebtn : ((ImageView) v).
                            setImageResource(R.drawable.imgbtn_online);break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!isInRange(event.getX(), event.getY())) {
                    switch (v.getId()) {
                        case R.id.singlebtn : ((ImageView) v).
                                setImageResource(R.drawable.imgbtn_single);break;
                        case R.id.onlinebtn : ((ImageView) v).
                                setImageResource(R.drawable.imgbtn_online);break;
                    }
                } else if (isInRange(event.getX(), event.getY())){
                    switch (v.getId()) {
                        case R.id.singlebtn : ((ImageView) v).
                                setImageResource(R.drawable.imgbtn_single_pre);break;
                        case R.id.onlinebtn : ((ImageView) v).
                                setImageResource(R.drawable.imgbtn_online_pre);break;
                    }
                }
            }
            return true;
        }
    }

}
