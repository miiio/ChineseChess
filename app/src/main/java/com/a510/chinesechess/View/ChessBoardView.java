package com.a510.chinesechess.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.a510.chinesechess.R;

/**
 * Created by Lao on 2018/1/6.
 */

public class ChessBoardView extends View{
    private int mViewHeight;
    private int mViewWidth;
    private float mChessBoardHeight;
    private float mChessBoardWidth;
    private float mChessSize;
    private float mChessBoardOffset; //棋盘边距的偏移

    private Resources mResources;
    private Bitmap mChessBitmap;
    private Bitmap mChessBoardBitmap; //0.065

    public ChessBoardView(Context context) {
        super(context);
    }

    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public ChessBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 自定义view的宽高测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = this.getHeight();
        mViewWidth = this.getWidth();
        if(mViewHeight !=0 && mViewWidth !=0 ){
            //计算棋盘的大小（高度是宽度的1.16倍）
            mChessBoardWidth = mViewWidth-20;
            mChessBoardHeight = (int) (mChessBoardWidth * 1.16f);

            //计算棋子的大小
            mChessSize = mChessBoardWidth / 9 - 10;


            //测量完成之后初始化资源
            initRes();
        }
    }

    /**
     * 初始化资源文件
     */
    private void initRes() {
        mResources = getResources();
        Matrix matrix;
        Bitmap bitmap;

        //棋盘资源的初始化
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chessboard)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessBoardWidth/bitmap.getWidth(),mChessBoardHeight/bitmap.getHeight()); //缩放尺寸
        mChessBoardBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap = null;

        //棋子底图资源的初始化
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_bg)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessSize/bitmap.getWidth(),mChessSize/bitmap.getHeight()); //缩放尺寸
        mChessBitmap = Bitmap.createBitmap (bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap = null;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChessBoard(canvas);
    }

    private void drawChessBoard(Canvas canvas) {
        float chessboardTop = (mViewHeight - mChessBoardHeight) / 2.5f;
        float chessboardLeft = 10;
        canvas.drawBitmap(mChessBoardBitmap,chessboardLeft,chessboardTop,null);
    }


}
