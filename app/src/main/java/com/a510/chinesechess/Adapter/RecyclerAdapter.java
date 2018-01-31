package com.a510.chinesechess.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by Chen on 2018/1/10.
 */

public class RecyclerAdapter extends CommonAdapter<String> {

    public RecyclerAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, String s, int position) {

    }
}
