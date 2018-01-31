package com.a510.chinesechess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Chen on 2018/1/8.
 */

public class MenuActivity extends AppCompatActivity {

    private ImageView mSingleBtn, mOnlineBtn;

    private void bindView() {
        mSingleBtn = (ImageView)findViewById(R.id.singlebtn);
        mOnlineBtn = (ImageView)findViewById(R.id.onlinebtn);

        mSingleBtn.setOnTouchListener(new TouchEvent());
        mOnlineBtn.setOnTouchListener(new TouchEvent());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //绑定控件
        bindView();

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
                        Intent intent = new Intent(MenuActivity.this,
                                OnlineActivity.class);
                        startActivity(intent);
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
