package com.a510.chinesechess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.a510.chinesechess.Adapter.RecyclerAdapter;
import com.a510.chinesechess.Fragment.CreatRoomFragment;
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
    private SearchView mSearchView;
    private ImageView mCreatBtn;
    private ImageView mExitBtn;
    private CreatRoomFragment mCreatRoomFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initData();
        initView();
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
        mAdapter = new RecyclerAdapter(this, R.layout.room_list_item, list);
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