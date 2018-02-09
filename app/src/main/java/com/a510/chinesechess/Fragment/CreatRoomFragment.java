package com.a510.chinesechess.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.a510.chinesechess.R;

/**
 * Created by Chen on 2018/2/7.
 */

public class CreatRoomFragment extends Fragment {

    private ImageView mClose;
    private RelativeLayout mRootLayout;
    private RelativeLayout mCreatMenu;
    private EditText mRoomName;
    private EditText mPassWord;
    private View mView;
    private ImageView mBeginCreat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_creat_room, container, false);
        initView();
        return mView;
    }

    private void initView() {

        //创建房间按钮
        mBeginCreat = mView.findViewById(R.id.btn_begin_creat);
        mBeginCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //房间名EditText
        mRoomName = mView.findViewById(R.id.room_name_edit);
        mPassWord = mView.findViewById(R.id.password_edit);

        //fragment根布局
        mCreatMenu = (RelativeLayout)mView.findViewById(R.id.creat_room_menu);
        mCreatMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRootLayout = (RelativeLayout)mView.findViewById(R.id.fragment_creat);
        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInputMethod();
                getActivity().onBackPressed();

            }
        });

        //关闭按钮
        mClose = (ImageView)mView.findViewById(R.id.btn_close_fragment);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInputMethod();
                getActivity().onBackPressed();
            }
        });
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager)getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
