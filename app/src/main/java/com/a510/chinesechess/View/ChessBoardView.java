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
import android.view.MotionEvent;
import android.view.View;

import com.a510.chinesechess.Bean.ChessBean;
import com.a510.chinesechess.R;
import com.a510.chinesechess.Util.ChessType;

/**
 * Created by Lao on 2018/1/6.
 */

public class ChessBoardView extends View {
    private Context mContext;

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

    private Point chess = new Point(-1, -1);
    private boolean ischecked = false;

    private Resources mResources;
    private Bitmap mChessBitmap;
    private Bitmap mChessCheckedBitmap[];
    private Bitmap mChessBoardBitmap;
    private Bitmap mChessTextBitmap[][][]; //是否被选中、阵营、棋子的类型
    private Bitmap mChessClickMark;

    //棋盘数据
    private ChessBean mChessData[][];
    //private ArrayList<ChessBean> mChessList;

    private boolean mTouched; //棋盘是否被选中
    private Point mTouchPoint; //选中的坐标
    private Point mCheckChess; //被选中的棋子
    private Point mMovingPoint; //当前移动的坐标
    private boolean mMoving;


    //画笔


    public ChessBoardView(Context context) {
        super(context);
    }

    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initChess();
    }


    public ChessBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 自定义view的宽高测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = this.getHeight();
        mViewWidth = this.getWidth();
        if (mViewHeight != 0 && mViewWidth != 0) {
            //计算棋盘的大小（高度是宽度的1.16倍）
            mChessBoardWidth = mViewWidth - 20;
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
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chessboard, null)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessBoardWidth / bitmap.getWidth(), mChessBoardHeight / bitmap.getHeight()); //缩放尺寸
        mChessBoardBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = null;

        //点击标志资源的初始化
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_click_mark, null)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(6*mChessRectSize / bitmap.getWidth(), 6*mChessRectSize / bitmap.getHeight());
        mChessClickMark = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        //棋子资源的初始化
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_bg, null)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessSize / bitmap.getWidth(), mChessSize / bitmap.getHeight()); //缩放尺寸
        mChessBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        mChessCheckedBitmap = new Bitmap[2];
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_bg_checked, null)).getBitmap();
        mChessCheckedBitmap[1] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_bg_checked_red, null)).getBitmap();
        mChessCheckedBitmap[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        mChessTextBitmap = new Bitmap[2][2][7];//是否被选中、阵营、棋子的类型
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                for(int k = 0; k<7; k++){
                    //chessijk
                    bitmap = ((BitmapDrawable) mResources
                            .getDrawable(getResourceByName( "chess"+String.valueOf(i)+
                                    String.valueOf(j)+String.valueOf(k)), null)).getBitmap();
                    mChessTextBitmap[i][j][k] = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            }
        }
    }


    private void initChess(){
        mTouched = false;
        mTouchPoint = new Point(-1,-1);
        mCheckChess = new Point(-1,-1);
        mMovingPoint = new Point(-1,-1);

        mChessData = new ChessBean[9][10];

        //红方
        mChessData[0][0] = new ChessBean(new Point(0,0),ChessType.ROOKS,ChessType.RED);
        mChessData[8][0] = new ChessBean(new Point(8,0),ChessType.ROOKS,ChessType.RED);
        mChessData[1][0] = new ChessBean(new Point(1,0),ChessType.KNIGHTS,ChessType.RED);
        mChessData[7][0] = new ChessBean(new Point(7,0),ChessType.KNIGHTS,ChessType.RED);
        mChessData[2][0] = new ChessBean(new Point(2,0),ChessType.ELEPHANTS,ChessType.RED);
        mChessData[6][0] = new ChessBean(new Point(6,0),ChessType.ELEPHANTS,ChessType.RED);
        mChessData[3][0] = new ChessBean(new Point(3,0),ChessType.MANDARINS,ChessType.RED);
        mChessData[5][0] = new ChessBean(new Point(5,0),ChessType.MANDARINS,ChessType.RED);
        mChessData[4][0] = new ChessBean(new Point(4,0),ChessType.KING,ChessType.RED);
        mChessData[1][2] = new ChessBean(new Point(1,2),ChessType.CANNONS,ChessType.RED);
        mChessData[7][2] = new ChessBean(new Point(7,2),ChessType.CANNONS,ChessType.RED);
        mChessData[0][3] = new ChessBean(new Point(0,3),ChessType.PAWNS,ChessType.RED);
        mChessData[2][3] = new ChessBean(new Point(2,3),ChessType.PAWNS,ChessType.RED);
        mChessData[4][3] = new ChessBean(new Point(4,3),ChessType.PAWNS,ChessType.RED);
        mChessData[6][3] = new ChessBean(new Point(6,3),ChessType.PAWNS,ChessType.RED);
        mChessData[8][3] = new ChessBean(new Point(8,3),ChessType.PAWNS,ChessType.RED);


        //黑方
        mChessData[0][9] = new ChessBean(new Point(0,9),ChessType.ROOKS,ChessType.BLACK);
        mChessData[8][9] = new ChessBean(new Point(8,9),ChessType.ROOKS,ChessType.BLACK);
        mChessData[1][9] = new ChessBean(new Point(1,9),ChessType.KNIGHTS,ChessType.BLACK);
        mChessData[7][9] = new ChessBean(new Point(7,9),ChessType.KNIGHTS,ChessType.BLACK);
        mChessData[2][9] = new ChessBean(new Point(2,9),ChessType.ELEPHANTS,ChessType.BLACK);
        mChessData[6][9] = new ChessBean(new Point(6,9),ChessType.ELEPHANTS,ChessType.BLACK);
        mChessData[3][9] = new ChessBean(new Point(3,9),ChessType.MANDARINS,ChessType.BLACK);
        mChessData[5][9] = new ChessBean(new Point(5,9),ChessType.MANDARINS,ChessType.BLACK);
        mChessData[4][9] = new ChessBean(new Point(4,9),ChessType.KING,ChessType.BLACK);
        mChessData[1][7] = new ChessBean(new Point(1,7),ChessType.CANNONS,ChessType.BLACK);
        mChessData[7][7] = new ChessBean(new Point(7,7),ChessType.CANNONS,ChessType.BLACK);
        mChessData[0][6] = new ChessBean(new Point(0,6),ChessType.PAWNS,ChessType.BLACK);
        mChessData[2][6] = new ChessBean(new Point(2,6),ChessType.PAWNS,ChessType.BLACK);
        mChessData[4][6] = new ChessBean(new Point(4,6),ChessType.PAWNS,ChessType.BLACK);
        mChessData[6][6] = new ChessBean(new Point(6,6),ChessType.PAWNS,ChessType.BLACK);
        mChessData[8][6] = new ChessBean(new Point(8,6),ChessType.PAWNS,ChessType.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制棋盘
        drawChessBoard(canvas);

        //绘制点击特效
        drawClickRect(canvas);


        //绘制棋子
        for(int i = 0; i<9; i++){
            for(int j = 0; j<10; j++){
                if(mChessData[i][j]!=null){
                    if(mMoving&&mCheckChess.equals(i,j)){
                        drawMovingChess(canvas,mChessData[i][j],mMovingPoint);
                    }else{
                        drawChess(canvas,mChessData[i][j],mCheckChess.equals(i,j));
                    }

                }
            }
        }
    }
    private void drawClickRect(Canvas canvas){
        if( !mTouched || mTouchPoint.equals(-1,-1)) {
            return;
        }
        Point point = CBCoordToViewCoord(new Point(mTouchPoint.x-3, mTouchPoint.y-3));
        canvas.drawBitmap(mChessClickMark,point.x,point.y,null);

    }

    private void drawMovingChess(Canvas canvas, ChessBean chess,Point point) {
        Bitmap bitmap = mChessTextBitmap[1][chess.getColor()][chess.getType()];

        canvas.drawBitmap(mChessCheckedBitmap[chess.getColor()]
                , point.x - mChessCheckedBitmap[chess.getColor()].getWidth() / 2
                , point.y - mChessCheckedBitmap[chess.getColor()].getHeight() / 2, null);
        //选中状态下的棋子上的子应该往上偏移
        canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2
                , point.y - bitmap.getHeight() / 2
                        -(chess.getColor()==ChessType.BLACK? 10: -4)
                , null);


    }

    private void drawChess(Canvas canvas, ChessBean chess,boolean isChecked) {
        Bitmap bitmap = mChessTextBitmap[isChecked?1:0][chess.getColor()][chess.getType()];
        Point point = CBCoordToViewCoord(chess.getCoord());
        if(isChecked){

            canvas.drawBitmap(mChessCheckedBitmap[chess.getColor()]
                    , point.x - mChessCheckedBitmap[chess.getColor()].getWidth() / 2
                    , point.y - mChessCheckedBitmap[chess.getColor()].getHeight() / 2, null);
            //选中状态下的棋子上的子应该往上偏移
            canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2
                    , point.y - bitmap.getHeight() / 2
                            -(chess.getColor()==ChessType.BLACK? 10: -4)
                    , null);

        }else{
            canvas.drawBitmap(mChessBitmap,point.x-mChessBitmap.getWidth()/2
                    ,point.y-mChessBitmap.getHeight()/2,null);
            canvas.drawBitmap(bitmap,point.x-bitmap.getWidth()/2
                    ,point.y-bitmap.getHeight()/2,null);
        }
    }

    private void drawChessBoard(Canvas canvas) {
        mChessboardTop = (mViewHeight - mChessBoardHeight) / 2.5f;
        mChessboardLeft = 10;
        canvas.drawBitmap(mChessBoardBitmap, mChessboardLeft, mChessboardTop, null);
    }

    /**
     * 将棋子的在棋盘中的坐标转换成真实坐标
     *
     * @param point 棋子的坐标
     * @return
     */
    private Point CBCoordToViewCoord(Point point) {
        return new Point((int) mChessboardLeft + (int) mChessBoardLeftOffset + point.x * (int) mChessRectSize
                , (int) mChessboardTop + (int) mChessBoardTopOffset + point.y * (int) mChessRectSize);
    }

    /**
     * 将真实坐标转换成棋子的在棋盘中的坐标
     *
     * @param point 屏幕上的坐标
     * @return
     */
    private Point ViewCoordToCBCoord(Point point) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                Point p = CBCoordToViewCoord(new Point(i, j));
                if ((point.x - p.x) * (point.x - p.x) + (point.y - p.y) * (point.y - p.y) <=
                        (mChessRectSize * mChessRectSize) / 2f) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(-1, -1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point((int)event.getX(),(int)event.getY());
        Point chessPoint;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                chessPoint = ViewCoordToCBCoord(point);
                if(!chessPoint.equals(-1,-1)){
                    mTouchPoint = chessPoint;
                    mTouched = true;
                    mCheckChess.set(chessPoint.x,chessPoint.y);
                }else{
                    mTouchPoint.set(-1,-1);
                    mTouched = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                chessPoint = ViewCoordToCBCoord(point);
                if(!chessPoint.equals(-1,-1)){
                    mTouchPoint = chessPoint;
                    mTouched = true;
                    if(!mCheckChess.equals(-1,-1)){
                        mMoving = true;
                        mMovingPoint.set(point.x,point.y);
                    }
                }else{
                    mTouchPoint.set(-1,-1);
                    mTouched = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                mMoving = false;
                mTouchPoint.set(-1,-1);
                mTouched = false;
                break;

        }

        invalidate();
        return true;
    }


    public int getResourceByName(String imageName) {
        int resId = getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        return resId;
    }

}