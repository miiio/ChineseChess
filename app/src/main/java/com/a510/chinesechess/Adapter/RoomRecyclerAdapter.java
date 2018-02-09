package com.a510.chinesechess.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.a510.chinesechess.Bean.RoomsBean;
import com.a510.chinesechess.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by Chen on 2018/1/10.
 */

public class RoomRecyclerAdapter extends CommonAdapter<RoomsBean.DataBean> {

    public RoomRecyclerAdapter(Context context, int layoutId, List<RoomsBean.DataBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, RoomsBean.DataBean dataBean, int position) {
        holder.setText(R.id.room_title,dataBean.getName());
        holder.setText(R.id.room_id,"ID: "+dataBean.getId());
        if(dataBean.getPw().equals("")){
            holder.setVisible(R.id.lock_id,false);
        }
        holder.setText(R.id.time_pass,dataBean.getDuration()+"分钟场");
        holder.setText(R.id.player_num,dataBean.getPlayer().size()+"/2");
        holder.setText(R.id.wait_state,dataBean.getStatus());
    }
}
