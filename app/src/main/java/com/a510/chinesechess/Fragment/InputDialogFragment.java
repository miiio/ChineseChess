package com.a510.chinesechess.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.a510.chinesechess.R;

/**
 * 要求输入内容的对话框
 * Created by Lao on 2018/2/9.
 */

public class InputDialogFragment extends DialogFragment{
    private String mMsg = null;
    private String mEditHint = null;
    private String mDefaultEditText = null;
    private InputDialogOnClickListener mCanelBtnOnClickListener = null;
    private InputDialogOnClickListener mOkBtnOnClickListener = null;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_input, container);
        if(mMsg != null) {
            ((TextView) view.findViewById(R.id.dialog_tv_msg)).setText(mMsg);
        }else{
             view.findViewById(R.id.dialog_tv_msg).setVisibility(View.GONE);
        }

        editText  = view.findViewById(R.id.dialog_edit);
        if(mEditHint!=null) {
            editText.setHint(mEditHint);
        }else{
            editText.setHint("");
        }

        if(mDefaultEditText!=null) {
            editText.setText(mDefaultEditText);
        }else{
            editText.setText("");
        }
        view.findViewById(R.id.dialog_btn_canel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCanelBtnOnClickListener != null) {
                    mCanelBtnOnClickListener.onClick(getEditText());
                }
                dismiss();
            }
        });

        view.findViewById(R.id.dialog_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOkBtnOnClickListener != null) {
                    mOkBtnOnClickListener.onClick(getEditText());
                }
                dismiss();

            }
        });
        return view;
    }

    public InputDialogFragment setMsg(String msg)
    {
        mMsg = msg;
        return this;
    }

    public InputDialogFragment setDefaultEditText(String text)
    {
        mDefaultEditText = text;
        return this;
    }

    public InputDialogFragment setHint(String hint)
    {
        mEditHint = hint;
        return this;
    }

    public InputDialogFragment setCanelBtnListener(InputDialogOnClickListener listener){
        this.mCanelBtnOnClickListener = listener;
        return this;
    }
    public InputDialogFragment setOkBtnListener(InputDialogOnClickListener listener){
        this.mOkBtnOnClickListener = listener;
        return this;
    }

    public String getEditText(){
        return editText.getText().toString();
    }

    public interface InputDialogOnClickListener{
        void onClick(String editText);
    }
}
