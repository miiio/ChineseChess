package com.a510.chinesechess.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.a510.chinesechess.Bean.ChessBean;
import com.a510.chinesechess.R;

/**
 * Created by Lao on 2018/1/6.
 */

public class ChessBoardView extends View{
    private int mViewHeight; //控件大小
    private int mViewWidth;
    private float mChessBoardHeight; //棋盘大小
    private float mChessBoardWidth;
    private float mChessSize; //棋子大小
    private float mChessBoardLeftOffset; //棋盘边距
    private float mChessBoardTopOffset;
    private float mChessboardTop; //棋盘位置
    private float mChessboardLeft;
    private float mChessRectSize; //棋盘上格子的大小

    private Point chess = new Point(-1,-1);

    private Resources mResources;
    private Bitmap mChessBitmap;
    private Bitmap mChessBoardBitmap;

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

            mChessBoardLeftOffset = mChessBoardWidth * 0.058f;
            mChessBoardTopOffset = mChessBoardHeight * 0.053f;

            //计算棋子的大小
            mChessRectSize = (mChessBoardWidth - 2 * mChessBoardLeftOffset) / 8;
            mChessSize = mChessRectSize - 10;



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
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chessboard ,null)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessBoardWidth/bitmap.getWidth(),mChessBoardHeight/bitmap.getHeight()); //缩放尺寸
        mChessBoardBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap = null;

        //棋子底图资源的初始化
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_bg ,null)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessSize/bitmap.getWidth(),mChessSize/bitmap.getHeight()); //缩放尺寸
        mChessBitmap = Bitmap.createBitmap (bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap = null;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChessBoard(canvas);

        if(chess.x!=-1&&chess.y!=-1){
            Point p = CBCoordToViewCoord(chess);
            canvas.drawBitmap(mChessBitmap,p.x- mChessRectSize /2,p.y- mChessRectSize /2,null);
        }
    }

    private void drawChess(Canvas canvas, ChessBean chess){

    }

    private void drawChessBoard(Canvas canvas) {
        mChessboardTop = (mViewHeight - mChessBoardHeight) / 2.5f;
        mChessboardLeft = 10;
        canvas.drawBitmap(mChessBoardBitmap,mChessboardLeft,mChessboardTop,null);
    }

    /**
     * 将棋子的在棋盘中的坐标转换成真实坐标
     * @param point 棋子的坐标
     * @return
     */
    private Point CBCoordToViewCoord(Point point){
        return new Point((int)mChessboardLeft+(int)mChessBoardLeftOffset+point.x*(int) mChessRectSize
                ,(int)mChessboardTop+(int) mChessBoardTopOffset +point.y*(int) mChessRectSize);
    }

    /**
     * 将真实坐标转换成棋子的在棋盘中的坐标
     * @param point 屏幕上的坐标
     * @return
     */
    private Point ViewCoordToCBCoord(Point point){
        for(int i = 0; i<9; i++) {
            for (int j = 0; j < 10; j++) {
                Point p = CBCoordToViewCoord(new Point(i, j));
                if ((point.x - p.x) * (point.x - p.x) + (point.y - p.y) * (point.y - p.y) <=
                        (mChessRectSize / 2f) * (mChessRectSize / 2f)) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(-1,-1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() ==  MotionEvent.ACTION_DOWN){
            Point point = new Point((int)event.getX(),(int)event.getY());
            chess = ViewCoordToCBCoord(point);
            Log.v("chess",chess.x+","+chess.y);
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
