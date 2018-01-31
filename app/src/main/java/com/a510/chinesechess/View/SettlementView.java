package com.a510.chinesechess.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a510.chinesechess.R;

/**
 * Created by Chen on 2018/1/10.
 */

public class SettlementView extends LinearLayout {

    private TextView mStep;
    private TextView mTime;
    private LinearLayout mRootLayout;

    public SettlementView(Context context, int stepNumber, int timeCost) {
        super(context);
        initView(context);

        mStep.setText(String.valueOf(stepNumber) + "步");

        int minutes, seconds;
        minutes = timeCost / 60;
        seconds = timeCost % 60;

        mTime.setText((minutes == 0 ? "" : String.valueOf(minutes) + "分")
                + String.valueOf(seconds) + "秒");
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.settlement_view,
                this, true);
        mStep = (TextView)findViewById(R.id.stepnumber);
        mTime = (TextView)findViewById(R.id.timecost);
        mRootLayout = (LinearLayout)findViewById(R.id.settlement_view_root);
    }
}
