package com.a510.chinesechess;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.a510.chinesechess.Adapter.RoomRecyclerAdapter;
import com.a510.chinesechess.Bean.RoomsBean;
import com.a510.chinesechess.Fragment.CreatRoomFragment;
import com.google.gson.Gson;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.SocketResponsePacket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2018/1/9.
 */

public class OnlineActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RoomRecyclerAdapter mAdapter;
    private SearchView mSearchView;
    private ImageView mCreatBtn;
    private ImageView mExitBtn;
    private CreatRoomFragment mCreatRoomFragment;
    private SocketClient socketClient;
    private List<RoomsBean.DataBean> mRooms;

    private SocketClient.SocketDelegate socketDelegate = new SocketClient.SocketDelegate() {
        @Override
        public void onConnected(SocketClient client) {

        }

        @Override
        public void onDisconnected(SocketClient client) {
            Toast.makeText(OnlineActivity.this,"与服务器断开连接",Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
            String json = responsePacket.getMessage();
            if(json == null){
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(json);
                String data = jsonObject.get("data").toString();
                switch (jsonObject.getString("type")){
                    case "all_rooms":
                        //加载房间列表
                        mRooms.clear();
                        mRooms.addAll(new Gson().fromJson(jsonObject.toString(),RoomsBean.class).getData());
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }catch (Exception e){

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initData();
        initView();
        socketClient = ((MyApplication)getApplication()).getSocketClient();
        socketClient.registerSocketDelegate(socketDelegate);
        loadRooms();
    }

    /**
     * 加载房间列表
     */
    public void loadRooms(){
        socketClient.sendString("{\"mod\":\"Room\", \"act\":\"getPage\",\"args\":{\"page\":1}}");
    }

    private void initView() {
        mCreatBtn = (ImageView)findViewById(R.id.btn_creat_room);
        mCreatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreatRoomFragment = new CreatRoomFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.add(R.id.fragment_root, mCreatRoomFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mExitBtn = (ImageView)findViewById(R.id.btn_exit);
        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_id);
        mRooms = new ArrayList<>();
        mAdapter = new RoomRecyclerAdapter(this, R.layout.room_list_item, mRooms);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mSearchView = (SearchView)findViewById(R.id.search_view);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchView.setFocusable(true);
        mSearchView.setFocusableInTouchMode(true);
    }

    private List<String> initData() {
        for(int i=0;i<20;i++){
            list.add("第" + i + "个数据");
        }
        return list;
    }
}