package com.a510.chinesechess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.a510.chinesechess.Adapter.RecyclerAdapter;
import com.a510.chinesechess.View.SettlementView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2018/1/9.
 */

public class OnlineActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initData();

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_id);
        mAdapter = new RecyclerAdapter(this, R.layout.room_list_item, list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<String> initData() {
        for(int i=0;i<20;i++){
            list.add("第" + i + "个数据");
        }
        return list;
    }
}