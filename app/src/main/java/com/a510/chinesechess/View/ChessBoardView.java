package com.a510.chinesechess.View;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.a510.chinesechess.Bean.ChessBean;
import com.a510.chinesechess.R;
import com.a510.chinesechess.Util.ChessType;
import com.a510.chinesechess.Util.PointEvaluator;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Lao on 2018/1/6.
 */

public abstract class ChessBoardView extends View {
    protected Context mContext;

    protected int mViewHeight; //控件大小
    protected int mViewWidth;
    protected float mChessBoardHeight; //棋盘大小
    protected float mChessBoardWidth;
    protected float mChessSize; //棋子大小
    protected float mChessBoardLeftOffset; //棋盘边距
    protected float mChessBoardTopOffset;
    protected float mChessboardTop; //棋盘位置
    protected float mChessboardLeft;
    protected float mChessRectSize; //棋盘上格子的大小

    protected Resources mResources;
    protected Bitmap mChessBitmap;
    protected Bitmap mChessCheckedBitmap[];
    protected Bitmap mChessBoardBitmap;
    protected Bitmap mChessTextBitmap[][][]; //是否被选中、阵营、棋子的类型
    protected Bitmap mChessClickMark;
    protected Bitmap mChessHintBitmap;
    protected Bitmap mPreMoveMarkBitmap[];
    protected Bitmap mWinTextBitmap[];
    protected Bitmap mSettlementBgBitmap;
    protected Bitmap mCloseBtnBitmap;
    protected Bitmap mCollectionBitmap;
    protected Bitmap mAgainBtnBitmap;
    protected Bitmap[] mRestartBtnBitmap;
    protected Bitmap[] mUndoBtnBitmap;

    protected Rect mCloseBtnRect;
    protected Rect mCollectionBtnRect;
    protected Rect mAgainBtnRect;
    protected Rect mRestartBtnRect;
    protected Rect mUndoBtnRect;

    protected int mUndoBtnStatus = 0;
    protected int mRestartBtnStatus = 0;

    //动画相关
    protected ValueAnimator mCheckAnimator;
    protected ValueAnimator mKillAnimator;
    protected ValueAnimator mEatChessAnimator;
    protected ValueAnimator mMoveChessAnimator;
    protected ValueAnimator mGreenClothsAnimator;
    protected ValueAnimator mGameOverAnimator;

    protected Bitmap mCheckAnimBitmap[]; //将军动画
    protected Bitmap mCenterAnim = null; //一个居中于屏幕的动画

    protected Bitmap mKillAnimBitmap[]; //绝杀动画

    protected Bitmap mEatChessBitmap[]; //吃子动画
    protected Bitmap mEatChessAnim = null; //用于显示吃子动画

    protected Bitmap mGreenClothsBitmap;
    protected Bitmap mGameOverBitmap;

    protected Point mMovingChessPoint;
    protected Point mMovingAnimPoint;

    protected float mChessBoardCacheTop;
    protected float mGreenClothsTop;
    protected float mAgainBtnTop;
    protected float mGameOverTop;
    protected float mGameOverAnimScaleValue; //结束动画的缩放倍数

    //棋盘数据
    protected ChessBean mChessData[][];
    //protected ArrayList<ChessBean> mChessList;

    protected int mBottomColor; //位于下方棋子的颜色
    protected int mTopColor; //位于上方
    protected boolean mTouched; //手指是否触摸屏幕
    protected Point mTouchPoint; //手指触摸屏幕的坐标
    protected Point mCheckChess; //被选中的棋子
    protected Point mMovingPoint; //当前移动的坐标
    protected boolean mMoving;
    protected int mWinner;
    protected boolean mGameOver;
    protected Bitmap mChessBoardCacheBitmap;

    protected int mCurColor; //当前轮到什么颜色下

    protected Point mPreMovePoint[]; //记录上一次移动

    protected int mChessCounter; //计步
    protected long mTimeCounter; //计时

    protected Stack<String> mChessDataStack;


    public ChessBoardView(Context context) {
        super(context);
    }

    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initChess();
        //initAnimator(); 应该在资源初始化完成后再调用
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

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        mViewWidth = w;
        if (mViewHeight != 0 && mViewWidth != 0) {
            //计算棋盘的大小（高度是宽度的1.16倍）
            mChessBoardWidth = mViewWidth - 20;
            mChessBoardHeight = (int) (mChessBoardWidth * 1.16f);

            mChessBoardLeftOffset = mChessBoardWidth * 0.058f;
            mChessBoardTopOffset = mChessBoardHeight * 0.053f;

            //计算棋子的大小
            mChessRectSize = (mChessBoardWidth - 2 * mChessBoardLeftOffset) / 8;
            mChessSize = mChessRectSize - 10;

            mChessBoardCacheTop = (mViewHeight - mChessBoardHeight) / 1.75f;
            //测量完成之后初始化资源
            initRes();
        }
    }

    /**
     * 初始化资源文件
     */
    protected void initRes() {
        mResources = getResources();
        Matrix matrix;
        Bitmap bitmap;

        //棋盘资源的初始化
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chessboard, null)).getBitmap();
        matrix = new Matrix();
        matrix.postScale(mChessBoardWidth / bitmap.getWidth(), mChessBoardHeight / bitmap.getHeight()); //缩放尺寸
        mChessBoardBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //动画资源的初始化(用的和棋盘一样的缩放尺寸)
        mCheckAnimBitmap = new Bitmap[13];
        for(int i = 1; i<=12; i++){
            bitmap = ((BitmapDrawable) mResources
                    .getDrawable(getResourceByName("anim_check_"+String.valueOf(i)), null))
                    .getBitmap();
            mCheckAnimBitmap[i] = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        mKillAnimBitmap = new Bitmap[15];
        for(int i = 1; i<=14; i++){
            bitmap = ((BitmapDrawable) mResources
                    .getDrawable(getResourceByName("anim_kill_"+String.valueOf(i)), null))
                    .getBitmap();
            mKillAnimBitmap[i] = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        mEatChessBitmap = new Bitmap[4];
        for(int i = 1; i<=3; i++){
            bitmap = ((BitmapDrawable) mResources
                    .getDrawable(getResourceByName("animate_eatchess_"+String.valueOf(i)), null))
                    .getBitmap();
            mEatChessBitmap[i] = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.green_cloths, null)).getBitmap();
        mGreenClothsBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.game_over, null)).getBitmap();
        mGameOverBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        mWinTextBitmap = new Bitmap[2];
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.redwin, null)).getBitmap();
        mWinTextBitmap[ChessType.RED] = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.blackwin, null)).getBitmap();
        mWinTextBitmap[ChessType.BLACK] = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.bg_settlement, null)).getBitmap();
        mSettlementBgBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.btn_close, null)).getBitmap();
        mCloseBtnBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.collection, null)).getBitmap();
        mCollectionBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //按钮
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.btn_again, null)).getBitmap();
        matrix.setScale((mViewWidth*0.34f)/bitmap.getWidth(),(mViewHeight*0.07f)/bitmap.getHeight());
        mAgainBtnBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

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

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.chess_hint, null)).getBitmap();
        mChessHintBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

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


        mRestartBtnBitmap = new Bitmap[2];
        mUndoBtnBitmap = new Bitmap[2];

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.restart_normal, null)).getBitmap();
        mRestartBtnBitmap[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.restart_press, null)).getBitmap();
        mRestartBtnBitmap[1] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.undo_normal, null)).getBitmap();
        mUndoBtnBitmap[0] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.undo_press, null)).getBitmap();
        mUndoBtnBitmap[1] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        mPreMoveMarkBitmap = new Bitmap[2];
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.prechess0,null)).getBitmap();
        mPreMoveMarkBitmap[0]= Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.prechess1,null)).getBitmap();
        mPreMoveMarkBitmap[1]= Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        initAnimator();
    }

    protected void initChess(){
        setBackgroundResource(R.drawable.chessboard_bg);
        mBottomColor = ChessType.RED; //默认红色
        mTopColor = ChessType.BLACK;

        mChessDataStack = new Stack<>();
        mCurColor = ChessType.RED;
        mWinner = -1;
        mGreenClothsTop = -1;
        mGameOverTop = -1;
        mGameOver = false;
        mTouched = false;
        mMovingChessPoint = new Point(-1,-1);
        mTouchPoint = new Point(-1,-1);
        mCheckChess = new Point(-1,-1);
        mMovingPoint = new Point(-1,-1);
        mPreMovePoint = new Point[2];
        mPreMovePoint[0] = new Point(-1,-1);
        mPreMovePoint[1] = new Point(-1,-1);
        mTimeCounter = System.currentTimeMillis();
        mChessCounter = 0;
        mChessData = new ChessBean[9][10];

        //乙方
        mChessData[0][0] = new ChessBean(new Point(0,0),ChessType.ROOKS, mTopColor);
        mChessData[8][0] = new ChessBean(new Point(8,0),ChessType.ROOKS, mTopColor);
        mChessData[1][0] = new ChessBean(new Point(1,0),ChessType.KNIGHTS, mTopColor);
        mChessData[7][0] = new ChessBean(new Point(7,0),ChessType.KNIGHTS, mTopColor);
        mChessData[2][0] = new ChessBean(new Point(2,0),ChessType.ELEPHANTS, mTopColor);
        mChessData[6][0] = new ChessBean(new Point(6,0),ChessType.ELEPHANTS, mTopColor);
        mChessData[3][0] = new ChessBean(new Point(3,0),ChessType.MANDARINS, mTopColor);
        mChessData[5][0] = new ChessBean(new Point(5,0),ChessType.MANDARINS, mTopColor);
        mChessData[4][0] = new ChessBean(new Point(4,0),ChessType.KING, mTopColor);
        mChessData[1][2] = new ChessBean(new Point(1,2),ChessType.CANNONS, mTopColor);
        mChessData[7][2] = new ChessBean(new Point(7,2),ChessType.CANNONS, mTopColor);
        mChessData[0][3] = new ChessBean(new Point(0,3),ChessType.PAWNS, mTopColor);
        mChessData[2][3] = new ChessBean(new Point(2,3),ChessType.PAWNS, mTopColor);
        mChessData[4][3] = new ChessBean(new Point(4,3),ChessType.PAWNS, mTopColor);
        mChessData[6][3] = new ChessBean(new Point(6,3),ChessType.PAWNS, mTopColor);
        mChessData[8][3] = new ChessBean(new Point(8,3),ChessType.PAWNS, mTopColor);


        //己方
        mChessData[0][9] = new ChessBean(new Point(0,9),ChessType.ROOKS, mBottomColor);
        mChessData[8][9] = new ChessBean(new Point(8,9),ChessType.ROOKS, mBottomColor);
        mChessData[1][9] = new ChessBean(new Point(1,9),ChessType.KNIGHTS, mBottomColor);
        mChessData[7][9] = new ChessBean(new Point(7,9),ChessType.KNIGHTS, mBottomColor);
        mChessData[2][9] = new ChessBean(new Point(2,9),ChessType.ELEPHANTS, mBottomColor);
        mChessData[6][9] = new ChessBean(new Point(6,9),ChessType.ELEPHANTS, mBottomColor);
        mChessData[3][9] = new ChessBean(new Point(3,9),ChessType.MANDARINS, mBottomColor);
        mChessData[5][9] = new ChessBean(new Point(5,9),ChessType.MANDARINS, mBottomColor);
        mChessData[4][9] = new ChessBean(new Point(4,9),ChessType.KING, mBottomColor);
        mChessData[1][7] = new ChessBean(new Point(1,7),ChessType.CANNONS, mBottomColor);
        mChessData[7][7] = new ChessBean(new Point(7,7),ChessType.CANNONS, mBottomColor);
        mChessData[0][6] = new ChessBean(new Point(0,6),ChessType.PAWNS, mBottomColor);
        mChessData[2][6] = new ChessBean(new Point(2,6),ChessType.PAWNS, mBottomColor);
        mChessData[4][6] = new ChessBean(new Point(4,6),ChessType.PAWNS, mBottomColor);
        mChessData[6][6] = new ChessBean(new Point(6,6),ChessType.PAWNS, mBottomColor);
        mChessData[8][6] = new ChessBean(new Point(8,6),ChessType.PAWNS, mBottomColor);

        mChessDataStack.push(getStrData());
    }

    protected void initAnimator(){
        //将军
        mCheckAnimator = ValueAnimator.ofInt(1,15); //多显示3帧
        mCheckAnimator.setDuration(1500);
        mCheckAnimator.setInterpolator(new LinearInterpolator());
        mCheckAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCenterAnim = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mCenterAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mCheckAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if(value>12){
                    value = 12;
                }
                mCenterAnim = mCheckAnimBitmap[value];
                invalidate();
            }
        });

        //绝杀
        mKillAnimator = ValueAnimator.ofInt(1,17);
        mKillAnimator.setDuration(1800);
        mKillAnimator.setInterpolator(new LinearInterpolator());
        mKillAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCenterAnim = null;
                gameOver(mWinner);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mCenterAnim = null;
                gameOver(mWinner);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mKillAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if(value>14){
                    value = 14;
                }
                mCenterAnim = mKillAnimBitmap[value];
                invalidate();
            }
        });

        //吃子
        mEatChessAnimator = ValueAnimator.ofInt(1,3);
        mEatChessAnimator.setDuration(200);
        mEatChessAnimator.setInterpolator(new LinearInterpolator());
        mEatChessAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mEatChessAnim = null;
                if(mWinner!=-1){
                    gameOver(mWinner);
                }else{
                    onStep();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mEatChessAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mEatChessAnim = mEatChessBitmap[(int) valueAnimator.getAnimatedValue()];
                invalidate();
            }
        });


        mGreenClothsAnimator = ValueAnimator
                .ofFloat(mChessBoardCacheTop - mGreenClothsBitmap.getHeight()*1.1f
                        ,mChessBoardCacheTop - mGreenClothsBitmap.getHeight()*0.95f);
        mGreenClothsAnimator.setDuration(300);
        //mGreenClothsAnimator.setInterpolator(new LinearInterpolator());
        mGreenClothsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mGameOverTop = mChessBoardCacheTop - mGreenClothsBitmap.getHeight()*0.95f
                        + mGreenClothsBitmap.getHeight()/2-mGameOverBitmap.getHeight()/2;
                mGameOverAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mGreenClothsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mGreenClothsTop = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        mGameOverAnimator = ValueAnimator.ofFloat(3f,1f,1.2f,1f);
        mGameOverAnimator.setDuration(800);
        mGameOverAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mGameOverAnimScaleValue = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mGameOver){
            canvas.drawBitmap(mChessBoardCacheBitmap,(mViewWidth-mChessBoardCacheBitmap.getWidth())/2
                    ,mChessBoardCacheTop,null);
            if(mGreenClothsTop != -1) {
                canvas.drawBitmap(mGreenClothsBitmap, mViewWidth / 2 - mGreenClothsBitmap.getWidth() / 2,
                        mGreenClothsTop, null);
                canvas.drawBitmap(mWinTextBitmap[mWinner],mViewWidth/2-mWinTextBitmap[mWinner].getWidth()/2
                        , mGreenClothsTop, null);
            }

            if(mGameOverTop!=-1){ //green动画结束后再绘这个
                canvas.save();
                canvas.scale(mGameOverAnimScaleValue,mGameOverAnimScaleValue,mViewWidth / 2,mGameOverTop);
                canvas.drawBitmap(mGameOverBitmap,mViewWidth / 2-mGameOverBitmap.getWidth()/2,
                        mGameOverTop, null);
                canvas.restore();
            }

            //结算部分
            float settlementTop = mChessBoardCacheTop+mChessBoardCacheBitmap.getHeight()*1.07f;
            canvas.drawBitmap(mSettlementBgBitmap,mViewWidth/2-mSettlementBgBitmap.getWidth()/2,
                    settlementTop,null);

            //结算文字
            Paint paint = new Paint();
            paint.setColor(Color.rgb(179,168,164));
            //paint.setColor(Color.rgb(227,196,68)); 黄色
            paint.setAntiAlias(true);
            paint.setTextSize(55);
            paint.setStyle(Paint.Style.STROKE);
            String s1 = "全局用时：";
            String s2 = "共走子：";
            int sec = (int)(mTimeCounter/1000);
            String s11 = sec/60+"分钟"+sec%60+"秒";
            if(sec<60){
                s11 = sec+"秒";
            }
            String s22 = mChessCounter+"步";
            float len1 = paint.measureText(s1+s11);
            float len2 = paint.measureText(s2+s22);
            float text1Y = settlementTop+mSettlementBgBitmap.getHeight()/2-10;
            float text2Y = settlementTop+mSettlementBgBitmap.getHeight()/2 +(paint.descent()-paint.ascent());

            canvas.drawText(s1,mViewWidth/2-len1/2,text1Y,paint);
            canvas.drawText(s2,mViewWidth/2-len1/2+paint.measureText("全"), text2Y,paint);

            paint.setColor(Color.rgb(227,196,68)); //黄色
            canvas.drawText(s11,mViewWidth/2-len1/2+paint.measureText(s1),text1Y,paint);
            canvas.drawText(s22,mViewWidth/2-len1/2+paint.measureText("全")+paint.measureText(s2),
                    text2Y,paint);

            //绘制按钮
            canvas.drawBitmap(mCloseBtnBitmap,mViewWidth-mCloseBtnBitmap.getWidth()*1.2f,
                    mCloseBtnBitmap.getHeight()*0.2f,null);
            canvas.drawBitmap(mCollectionBitmap,mCloseBtnBitmap.getWidth()*0.2f
                    ,0,null);
            mAgainBtnTop = (mViewHeight-settlementTop-mSettlementBgBitmap.getHeight()
                    -mAgainBtnBitmap.getHeight())/2
                    +settlementTop+mSettlementBgBitmap.getHeight();
            canvas.drawBitmap(mAgainBtnBitmap,mViewWidth/2-mAgainBtnBitmap.getWidth()/2,
                    mAgainBtnTop,null);

            //计算按钮判断区域
            if(mCloseBtnRect==null) {
                mCloseBtnRect = new Rect();
            }
            if(mCollectionBtnRect==null){
                mCollectionBtnRect = new Rect();
            }
            if(mAgainBtnRect==null){
                mAgainBtnRect = new Rect();
            }
            int left = (int)(mViewWidth-mCloseBtnBitmap.getWidth()*1.2f);
            int top = (int)(mCloseBtnBitmap.getHeight()*0.2f);
            mCloseBtnRect.set(left, top, left+mCloseBtnBitmap.getWidth(),top+mCloseBtnBitmap.getHeight());
            left = (int)(mCloseBtnBitmap.getWidth()*0.2f);
            mCollectionBtnRect.set(left,0,left+mCollectionBitmap.getWidth(), mCollectionBitmap.getHeight());
            left = mViewWidth/2-mAgainBtnBitmap.getWidth()/2;
            top  = (int)mAgainBtnTop;
            mAgainBtnRect.set(left,top,mAgainBtnBitmap.getWidth()+left,mAgainBtnBitmap.getHeight()+top);

        }else{
            //绘制棋盘
            drawChessBoard(canvas);
            //绘制点击特效
            drawClickRect(canvas);
            //绘制棋子
            for(int i = 0; i<9; i++){
                for(int j = 0; j<10; j++){
                    if(mChessData[i][j]!=null){
                        if(mMoving&&mCheckChess.equals(i,j)
                                || mMoveChessAnimator!=null && mMoveChessAnimator.isRunning()
                                &&mMovingChessPoint.equals(i,j)){
                            //drawMovingChess(canvas,mChessData[i][j],mMovingPoint);
                        }else{
                            drawChess(canvas,mChessData[i][j],mCheckChess.equals(i,j));
                        }
                    }
                }
            }
            if(mMoving && mChessData[mCheckChess.x][mCheckChess.y]!=null) {
                drawMovingChess(canvas, mChessData[mCheckChess.x][mCheckChess.y], mMovingPoint);
            }
            //绘制绿点提示
            if(!mCheckChess.equals(-1,-1) && mChessData[mCheckChess.x][mCheckChess.y]!=null){
                drawChessHint(canvas,getChessHint(mChessData[mCheckChess.x][mCheckChess.y]));
            }
            //绘制上一步
            if(!mMoving && (mMoveChessAnimator == null || !mMoveChessAnimator.isRunning())
                    && !mPreMovePoint[0].equals(-1,-1)&&!mPreMovePoint[1].equals(-1,-1)){
                drawPreMoveMark(canvas,mPreMovePoint[0],mPreMovePoint[1]);
            }
            //动画部分
            //居中的动画
            if(mCenterAnim != null) {
                canvas.drawBitmap(mCenterAnim, mViewWidth / 2 - mCenterAnim.getWidth() / 2,
                        mViewHeight / 2 - mCenterAnim.getHeight() / 2, null);
            }
            if(mMoveChessAnimator!=null && mMoveChessAnimator.isRunning()){
                drawMovingChess(canvas,getChess(mMovingChessPoint),mMovingAnimPoint);
            }
            if(mEatChessAnim != null && mEatChessAnimator.isRunning()){
                Point p = CBCoordToViewCoord(getChess(mMovingChessPoint).getCoord());
                float left = p.x-mEatChessAnim.getWidth()/2;
                float top =  p.y-mChessBitmap.getWidth()/2-(mEatChessAnim.getHeight()-mChessBitmap.getWidth());
                canvas.drawBitmap(mEatChessAnim,left,top,null);
            }

            //绘制底部按钮
            if(mRestartBtnRect == null){
                int left = (int) (mViewWidth - mRestartBtnBitmap[0].getWidth()*2.2f);
                int top = (int) (mViewHeight-mRestartBtnBitmap[0].getHeight()*1.2f);
                mRestartBtnRect = new Rect(left,top,left+mRestartBtnBitmap[0].getWidth(),
                        mRestartBtnBitmap[0].getHeight()+top);
            }
            if(mUndoBtnRect == null){
                int left = (int) (mUndoBtnBitmap[0].getWidth()*1.2f);
                int top = (int) (mViewHeight-mUndoBtnBitmap[0].getHeight()*1.2f);
                mUndoBtnRect = new Rect(left,top,left+mUndoBtnBitmap[0].getWidth(),
                        mUndoBtnBitmap[0].getHeight()+top);
            }
            canvas.drawBitmap(mUndoBtnBitmap[mUndoBtnStatus]
                    ,mUndoBtnRect.left,mUndoBtnRect.top,null);
            canvas.drawBitmap(mRestartBtnBitmap[mRestartBtnStatus]
                    ,mRestartBtnRect.left,mRestartBtnRect.top,null);
        }

    }

    /**
     * 绘制上一步移动的标志
     * @param canvas
     * @param p1
     * @param p2
     */
    protected void drawPreMoveMark(Canvas canvas,Point p1, Point p2){
        p1 = CBCoordToViewCoord(p1);
        p2 = CBCoordToViewCoord(p2);
        canvas.drawBitmap(mPreMoveMarkBitmap[0],p1.x-mPreMoveMarkBitmap[0].getWidth()/2
                ,p1.y-mPreMoveMarkBitmap[0].getHeight()/2,null);

        canvas.drawBitmap(mPreMoveMarkBitmap[1],p2.x-mPreMoveMarkBitmap[1].getWidth()/2
                ,p2.y-mPreMoveMarkBitmap[1].getHeight()/2,null);
    }

    /**
     * 绘制选中光标
     * @param canvas
     */
    protected void drawClickRect(Canvas canvas){
        if( !mTouched || mTouchPoint.equals(-1,-1)) {
            return;
        }
        Point point = CBCoordToViewCoord(new Point(mTouchPoint.x-3, mTouchPoint.y-3));
        canvas.drawBitmap(mChessClickMark,point.x,point.y,null);

    }

    /**
     * 绘制绿点提示
     *
     * @param canvas
     * @param points 棋盘坐标
     */
    protected void drawChessHint(Canvas canvas, ArrayList<Point> points){
        for (Point point : points) {
            Point viewPoint = CBCoordToViewCoord(point);
            canvas.drawBitmap(mChessHintBitmap,viewPoint.x-mChessHintBitmap.getWidth()/2,
                    viewPoint.y-mChessHintBitmap.getHeight()/2,null);
        }
    }

    /**
     * 绘制移动中的棋子
     *
     * @param canvas
     * @param chess 棋子
     * @param point 真实坐标
     */
    protected void drawMovingChess(Canvas canvas, ChessBean chess,Point point) {
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

    /**
     * 绘制棋子
     *
     * @param canvas
     * @param chess 棋子
     * @param isChecked 是否被选中
     */
    protected void drawChess(Canvas canvas, ChessBean chess,boolean isChecked) {
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

    /**
     * 绘制棋盘
     * @param canvas
     */
    protected void drawChessBoard(Canvas canvas) {
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
    protected Point CBCoordToViewCoord(Point point) {
        return new Point((int) mChessboardLeft + (int) mChessBoardLeftOffset + point.x * (int) mChessRectSize
                , (int) mChessboardTop + (int) mChessBoardTopOffset + point.y * (int) mChessRectSize);
    }

    /**
     * 将真实坐标转换成棋子的在棋盘中的坐标
     *
     * @param point 屏幕上的坐标
     * @return
     */
    protected Point ViewCoordToCBCoord(Point point) {
        Point ret = new Point();
        float min = 999999;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                Point p = CBCoordToViewCoord(new Point(i, j));
                if ((point.x - p.x) * (point.x - p.x) + (point.y - p.y) * (point.y - p.y)<min){
                    min = (point.x - p.x) * (point.x - p.x) + (point.y - p.y) * (point.y - p.y);
                    ret.set(i,j);
                }

            }
        }
        if(min>mChessRectSize*mChessRectSize){
            ret.set(-1,-1);
        }
        return ret;
    }

    /**
     * 触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point((int)event.getX(),(int)event.getY());
        Point chessPoint = ViewCoordToCBCoord(point);
        if(mGameOver){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if(mCloseBtnRect.contains(point.x,point.y)){
                        //结束
                        try{
                            ((Activity)getContext()).onBackPressed();
                        }catch (Exception e){

                        };
                    }else if(mCollectionBtnRect.contains(point.x,point.y)){
                        //收藏
                    }else if(mAgainBtnRect.contains(point.x,point.y)){
                        //再来一局
                        initChess();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
            invalidate();

        }else{
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(!chessPoint.equals(-1,-1)){
                        mTouchPoint = chessPoint;
                        mTouched = true;
                        if(!isPointNull(mCheckChess)&&
                                getChessHint(getChess(mCheckChess)).contains(chessPoint)){
                            moveChess(getChess(mCheckChess),chessPoint,true); //通过点击来移动棋子
                        }else{
                            if(isPointNull(chessPoint)){
                                mCheckChess.set(-1,-1);
                            }else{
                                //选择的棋子是己方棋子
                                if(mCurColor == getChess(chessPoint).getColor()) {
                                    mCheckChess.set(chessPoint.x, chessPoint.y);
                                }
                            }
                        }
                    }else{
                        mTouchPoint.set(-1,-1);
                        mTouched = false;
                    }

                    if(mUndoBtnRect.contains(point.x,point.y)){
                        //悔棋
                        mUndoBtnStatus = 1;
                    }
                    if(mRestartBtnRect.contains(point.x,point.y)){
                        //重来
                        mRestartBtnStatus = 1;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
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
                    if(mUndoBtnRect.contains(point.x,point.y) && mUndoBtnStatus == 1){
                        //悔棋
                        mUndoBtnStatus = 0;
                        Undo(2);
                    }
                    if(mRestartBtnRect.contains(point.x,point.y) && mRestartBtnStatus == 1){
                        //重来
                        mRestartBtnStatus = 0;
                        initChess();
                        break;
                    }

                    //通过拖拽来移动棋子
                    if(mMoving && !mCheckChess.equals(chessPoint) && !isPointNull(mCheckChess)){
                        moveChess(mChessData[mCheckChess.x][mCheckChess.y],chessPoint,false);
                    }

                    if(mMoving && mCheckChess.equals(-1,-1)){
                        mCheckChess.set(mCheckChess.x,mCheckChess.y);
                    }

                    mMoving = false;
                    mTouchPoint.set(-1,-1);
                    mTouched = false;
                    break;

            }
            invalidate();
        }


        return true;
    }

    /**
     * 获取一个 棋盘 的缓存视图
     *
     * @return
     */
    protected Bitmap getCacheBitmapFromView() {
        final boolean drawingCacheEnabled = true;
        setDrawingCacheEnabled(drawingCacheEnabled);
        buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            Matrix matrix = new Matrix();
            matrix.postScale(0.82f,0.82f); //缩放尺寸
            bitmap = Bitmap.createBitmap(drawingCache,(int)mChessboardLeft
                    ,(int)mChessboardTop,(int)mChessBoardWidth, (int) (mChessBoardHeight*0.964f)
                    ,matrix,false);
            setDrawingCacheEnabled(false);


        } else {
            bitmap = null;
        }
        return bitmap;
    }

    protected void gameOver(int winner){
        mWinner = winner;
        mChessBoardCacheBitmap = getCacheBitmapFromView();
        setBackgroundColor(Color.rgb(43,35,32));
        mGameOver = true;
        mGreenClothsAnimator.start();
        mTimeCounter = System.currentTimeMillis()-mTimeCounter;

        invalidate();
    }

    /**
     * 悔棋
     * @param step
     */
    protected void Undo(int step){
        if(mChessDataStack!=null && mChessDataStack.size()>step){
            while(step-->0){
                mChessDataStack.pop();
            }
            setStrData(mChessDataStack.peek());
        }
    }

    /**
     * 移动一个棋子
     *
      * @param chess 被移动的棋子
     * @param point 移动的目的地
     */
    protected void moveChess(ChessBean chess, Point point, boolean startAnimator){
        if(point.equals(-1,-1) || chess==null || chess.getCoord().equals(point)
                || !getChessHint(chess).contains(point) ) {
            return;
        }
        mChessCounter++;
        boolean isEatChess = getChess(point)!=null;
        if(isEatChess && getChess(point)!= null && getChess(point).getType()==ChessType.KING){
            mWinner = chess.getColor();
        }
        mPreMovePoint[0].set(chess.getCoord().x,chess.getCoord().y);
        mPreMovePoint[1].set(point.x,point.y);

        Point tmpPoint = new Point(chess.getCoord().x,chess.getCoord().y);

        mChessData[point.x][point.y] = new ChessBean(new Point(point.x,point.y)
                , chess.getType(),chess.getColor());
        mChessData[tmpPoint.x][tmpPoint.y] = null;

        mCheckChess.set(-1,-1);

        if(checkGeneral(mChessData[point.x][point.y])){
            if(checkKill(mChessData[point.x][point.y].getColor())){
                mWinner = mChessData[point.x][point.y].getColor();
                mKillAnimator.start();
            }else{
                mCheckAnimator.start();
            }

        }
        mCurColor = getOpponentColor(mCurColor);
        //是否带动画
        if(startAnimator){
            mMovingChessPoint.set(point.x,point.y);
            Point startPoint = new Point(CBCoordToViewCoord(tmpPoint));
            Point endPoint = new Point(CBCoordToViewCoord(point));
            long duration = (long) (getPointDistance(tmpPoint,point) / 1.0 * 80); //动画时长
            mMoveChessAnimator = ValueAnimator.ofObject(new PointEvaluator(),startPoint,endPoint);
            mMoveChessAnimator.setDuration(duration);
            mMoveChessAnimator.setInterpolator(new LinearInterpolator());
            mMoveChessAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mMovingAnimPoint = (Point)animation.getAnimatedValue();
                    invalidate();
                }
            });
            if(isEatChess){
                mMoveChessAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mEatChessAnimator.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }else{
                mMoveChessAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        onStep();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
            mMoveChessAnimator.start();
        }else if(isEatChess){
            mMovingChessPoint.set(point.x,point.y);
            mEatChessAnimator.start();
        }else{
            onStep();
        }

        mChessDataStack.push(getStrData());
    }

    abstract void onStep();

    /**
     * 计算2个点直接的距离
     * @param p1
     * @param p2
     * @return
     */
    protected double getPointDistance(Point p1, Point p2){
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
    }


    /**
     * 获取一个棋子下一步能走的坐标
     * @param chess
     * @param bottomColor
     * @return
     */
    public static ArrayList<Point> getChessHint(ChessBean chess, ChessBean[][] chessData,int bottomColor){
        ArrayList<Point> points = new ArrayList<>();
        if(chess == null){
            return points;
        }
        int x = chess.getCoord().x;
        int y = chess.getCoord().y;

        int dir[][] = {{0,1},{0,-1},{1,0},{-1,0}}; //上下左右4个方向
        int dir2[][] = {{1,-1},{1,1},{-1,-1},{-1,1}}; //斜对角4个方向
        switch (chess.getType()) {
            case ChessType.ROOKS: //车
                for(int yy = y-1; yy>=0; yy--){ //up
                    if(chessData[x][yy] == null){//空白的地方
                        points.add(new Point(x,yy));
                    }else if(chessData[x][yy].getColor() == chess.getColor()){ //己方棋子
                        break;
                    }else{ //乙方棋子
                        points.add(new Point(x,yy));
                        break;
                    }
                }
                for(int yy = y+1; yy<10; yy++){ //down
                    if(chessData[x][yy] == null){//空白的地方
                        points.add(new Point(x,yy));
                    }else if(chessData[x][yy].getColor() == chess.getColor()){ //己方棋子
                        break;
                    }else{ //乙方棋子
                        points.add(new Point(x,yy));
                        break;
                    }
                }
                for(int xx = x-1; xx>=0; xx--){ //left
                    if(chessData[xx][y] == null){//空白的地方
                        points.add(new Point(xx,y));
                    }else if(chessData[xx][y].getColor() == chess.getColor()){ //己方棋子
                        break;
                    }else{ //乙方棋子
                        points.add(new Point(xx,y));
                        break;
                    }
                }
                for(int xx = x+1; xx<9; xx++){ //right
                    if(chessData[xx][y] == null){//空白的地方
                        points.add(new Point(xx,y));
                    }else if(chessData[xx][y].getColor() == chess.getColor()){ //己方棋子
                        break;
                    }else{ //乙方棋子
                        points.add(new Point(xx,y));
                        break;
                    }
                }
                break;
            case ChessType.KNIGHTS: //马
                if(checkPoint(x+1,y-2) && (chessData[x+1][y-2]==null
                        ||chessData[x+1][y-2].getColor()!=chess.getColor())
                        && chessData[x][y-1]==null){
                    points.add(new Point(x+1,y-2));
                }
                if(checkPoint(x+2,y-1) && (chessData[x+2][y-1]==null
                        ||chessData[x+2][y-1].getColor()!=chess.getColor())
                        && chessData[x+1][y]==null){
                    points.add(new Point(x+2,y-1));
                }
                if(checkPoint(x+1,y+2) && (chessData[x+1][y+2]==null
                        ||chessData[x+1][y+2].getColor()!=chess.getColor())
                        && chessData[x][y+1]==null){
                    points.add(new Point(x+1,y+2));
                }
                if(checkPoint(x+2,y+1) && (chessData[x+2][y+1]==null
                        ||chessData[x+2][y+1].getColor()!=chess.getColor())
                        && chessData[x+1][y]==null){
                    points.add(new Point(x+2,y+1));
                }
                if(checkPoint(x-1,y-2) && (chessData[x-1][y-2]==null
                        ||chessData[x-1][y-2].getColor()!=chess.getColor())
                        && chessData[x][y-1]==null){
                    points.add(new Point(x-1,y-2));
                }
                if(checkPoint(x-2,y-1) && (chessData[x-2][y-1]==null
                        ||chessData[x-2][y-1].getColor()!=chess.getColor())
                        && chessData[x-1][y]==null){
                    points.add(new Point(x-2,y-1));
                }
                if(checkPoint(x-1,y+2) && (chessData[x-1][y+2]==null
                        ||chessData[x-1][y+2].getColor()!=chess.getColor())
                        && chessData[x][y+1]==null){
                    points.add(new Point(x-1,y+2));
                }
                if(checkPoint(x-2,y+1) && (chessData[x-2][y+1]==null
                        ||chessData[x-2][y+1].getColor()!=chess.getColor())
                        && chessData[x-1][y]==null){
                    points.add(new Point(x-2,y+1));
                }
                break;
            case ChessType.ELEPHANTS: //象
                //上右
                if(checkPoint(x+2,y-2) && (chessData[x+2][y-2]==null
                        ||chessData[x+2][y-2].getColor()!=chess.getColor())
                        && chessData[x+1][y-1]==null && y!=5){
                    points.add(new Point(x+2,y-2));
                }

                //下右
                if(checkPoint(x+2,y+2) && (chessData[x+2][y+2]==null
                        ||chessData[x+2][y+2].getColor()!=chess.getColor())
                        && chessData[x+1][y+1]==null && y!=4){
                    points.add(new Point(x+2,y+2));
                }

                //上左
                if(checkPoint(x-2,y-2) && (chessData[x-2][y-2]==null
                        ||chessData[x-2][y-2].getColor()!=chess.getColor())
                        && chessData[x-1][y-1]==null && y!=5){
                    points.add(new Point(x-2,y-2));
                }

                //下左
                if(checkPoint(x-2,y+2) && (chessData[x-2][y+2]==null
                        ||chessData[x-2][y+2].getColor()!=chess.getColor())
                        && chessData[x-1][y+1]==null  && y!=4){
                    points.add(new Point(x-2,y+2));
                }
                break;
            case ChessType.MANDARINS: //士
                for(int i = 0; i<4; i++){
                    int xx = x+dir2[i][0];
                    int yy = y+dir2[i][1];
                    if(checkPoint(xx,yy) && checkInKing(xx,yy) && (chessData[xx][yy]==null
                            ||chessData[xx][yy].getColor()!=chess.getColor())){
                        points.add(new Point(xx,yy));
                    }
                }
                break;
            case ChessType.KING: //将
                //先找到对方将的位置
                Point GeneralPoint = new Point();
                for(int i = 0; i<9; i++){
                    for(int j = 0; j<10; j++) {
                        if(chessData[i][j] != null && chessData[i][j].getColor()!=chess.getColor()
                                && chessData[i][j].getType()==ChessType.KING){
                            GeneralPoint.set(i,j);
                            break;
                        }
                    }
                }
                boolean flag = false; //2个将之间是否有其他棋子
                for(int i = Math.min(chess.getCoord().y,GeneralPoint.y)+1;
                    i<Math.max(chess.getCoord().y,GeneralPoint.y); i++){
                    if(chessData[GeneralPoint.x][i]!=null){
                        flag = true;
                        break;
                    }
                }
                if(GeneralPoint.x==chess.getCoord().x && !flag){
                        points.add(new Point(GeneralPoint.x,GeneralPoint.y));
                        break;
                }
                for(int i = 0; i<4; i++){
                    int xx = x+dir[i][0];
                    int yy = y+dir[i][1];
                    if(checkPoint(xx,yy) && checkInKing(xx,yy) && (chessData[xx][yy]==null
                            ||chessData[xx][yy].getColor()!=chess.getColor())
                            && !(GeneralPoint.x==xx && !flag)){
                        points.add(new Point(xx,yy));
                    }
                }
                break;
            case ChessType.PAWNS: //兵
                if(chess.getColor() == bottomColor){
                    if(y<=4 && checkPoint(x-1,y) && (chessData[x-1][y]==null
                            ||chessData[x-1][y].getColor()!=chess.getColor())){ //已过河
                        points.add(new Point(x-1,y));
                    }
                    if(y<=4 && checkPoint(x+1,y) && (chessData[x+1][y]==null
                            ||chessData[x+1][y].getColor()!=chess.getColor())){ //已过河
                        points.add(new Point(x+1,y));
                    }
                    if(checkPoint(x,y-1) && (chessData[x][y-1]==null
                            ||chessData[x][y-1].getColor()!=chess.getColor())){
                        points.add(new Point(x,y-1));
                    }
                }else{
                    if(y>=5 && checkPoint(x-1,y) && (chessData[x-1][y]==null
                            ||chessData[x-1][y].getColor()!=chess.getColor())){ //已过河
                        points.add(new Point(x-1,y));
                    }
                    if(y>=5 && checkPoint(x+1,y) && (chessData[x+1][y]==null
                            ||chessData[x+1][y].getColor()!=chess.getColor())){ //已过河
                        points.add(new Point(x+1,y));
                    }
                    if(checkPoint(x,y+1) && (chessData[x][y+1]==null
                            ||chessData[x][y+1].getColor()!=chess.getColor())){
                        points.add(new Point(x,y+1));
                    }
                }
                break;

            case ChessType.CANNONS: //炮
                for(int yy = y-1; yy>=0; yy--){ //up
                    if(chessData[x][yy] == null){//空白的地方
                        points.add(new Point(x,yy));
                    }else { //找到一个棋子，现在要找这个棋子后面的乙方棋子
                        for(int yyy = yy-1; yyy>=0; yyy--){
                            if(chessData[x][yyy] != null){
                                if(chessData[x][yyy].getColor() != chess.getColor()){
                                    points.add(new Point(x,yyy));
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                for(int yy = y+1; yy<10; yy++){ //down
                    if(chessData[x][yy] == null){//空白的地方
                        points.add(new Point(x,yy));
                    }else { //找到一个棋子，现在要找这个棋子后面的乙方棋子
                        for(int yyy = yy+1; yyy<10; yyy++){
                            if(chessData[x][yyy] != null){
                                if(chessData[x][yyy].getColor() != chess.getColor()){
                                    points.add(new Point(x,yyy));
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                for(int xx = x-1; xx>=0; xx--){ //left
                    if(chessData[xx][y] == null){//空白的地方
                        points.add(new Point(xx,y));
                    }else { //找到一个棋子，现在要找这个棋子后面的乙方棋子
                        for(int xxx = xx-1; xxx>=0; xxx--){
                            if(chessData[xxx][y] != null){
                                if(chessData[xxx][y].getColor() != chess.getColor()){
                                    points.add(new Point(xxx,y));
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                for(int xx = x+1; xx<9; xx++){ //right
                    if(chessData[xx][y] == null){//空白的地方
                        points.add(new Point(xx,y));
                    }else  { //找到一个棋子，现在要找这个棋子后面的乙方棋子
                        for(int xxx = xx+1; xxx<9; xxx++){
                            if(chessData[xxx][y] != null){
                                if(chessData[xxx][y].getColor() != chess.getColor()){
                                    points.add(new Point(xxx,y));
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;

        }
        return points;
    }

    protected ArrayList<Point> getChessHint(ChessBean chess){
        return getChessHint(chess, mChessData, mBottomColor);
    }

    /**
     * 获取指定坐标的棋子
     *
     * @param point
     * @return
     */
    protected ChessBean getChess(Point point){
        if(point.equals(-1,-1)) {
            return null;
        }else {
            return mChessData[point.x][point.y];
        }
    }

    /**
     * 判断指定坐标是否没有棋子
     *
     * @param point
     * @return
     */
    protected boolean isPointNull(Point point){
        return point.equals(-1,-1)||mChessData[point.x][point.y]==null;
    }

    /**
     * 判断point（棋盘坐标）是否在在棋盘内
     * @param point
     * @return
     */
    protected boolean checkPoint(Point point){
        return (point.x>=0&&point.x<9&&point.y>=0&&point.y<10);
    }

    /**
     * 判断point（棋盘坐标）是否在在棋盘内
     * @param x
     * @param y
     * @return
     */
    public static boolean checkPoint(int x,int y){
        return (x>=0&&x<9&&y>=0&&y<10);
    }

    /**
     * 判断点是否在将区内
     * @param x
     * @param y
     * @return
     */
    public static boolean checkInKing(int x,int y){
        return (x>=3&&x<=5)&&(y>=0&&y<=2||y>=7&&y<=9);
    }

    /**
     * 获取相对颜色
     * @param color
     * @return
     */
    protected int getOpponentColor(int color){
        return color == ChessType.RED?ChessType.BLACK:ChessType.RED;
    }

    /**
     * 获取所有棋子的对象
     * @param color 要指定颜色,-1为所有颜色
     * @return
     */
    protected ArrayList<ChessBean> getAllChess(int color){
        ArrayList<ChessBean> chessBeanArrayList = new ArrayList<>();
        for(int i = 0; i<9; i++){
            for(int j = 0; j<10; j++) {
                if(mChessData[i][j]!=null) {
                    if (color == -1) {
                        chessBeanArrayList.add(mChessData[i][j]);
                    } else if (mChessData[i][j].getColor() == color) {
                        chessBeanArrayList.add(mChessData[i][j]);
                    }
                }
            }
        }
        return chessBeanArrayList;
    }

    /**
     * 判断制定棋子是否将军对手
     * @param chess
     * @return
     */
    protected boolean checkGeneral(ChessBean chess){
        //先找到对方将的位置
        Point GeneralPoint = new Point();
        for(int i = 0; i<9; i++){
            for(int j = 0; j<10; j++) {
                if(mChessData[i][j] != null && mChessData[i][j].getColor()!=chess.getColor()
                        && mChessData[i][j].getType()==ChessType.KING){
                    GeneralPoint.set(i,j);
                    break;
                }
            }
        }
        //判断棋子是否能吃到对方的将
        return getChessHint(chess).contains(GeneralPoint);
    }

    /**
     * 判断当前是否将军对手
     *
     * @param color 己方颜色
     * @return
     */
    protected boolean checkGeneral(int color){
        //先找到对方将的位置
        Point GeneralPoint = new Point();
        for(int i = 0; i<9; i++){
            for(int j = 0; j<10; j++) {
                if(mChessData[i][j] != null && mChessData[i][j].getColor()!=color
                        && mChessData[i][j].getType()==ChessType.KING){
                    GeneralPoint.set(i,j);
                    break;
                }
            }
        }
        //判断己方所以棋子是否能吃到对方的将
        for (ChessBean chess:getAllChess(color)) {
            if(getChessHint(chess).contains(GeneralPoint)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否绝杀对方
     * @param color 己方颜色
     * @return
     */
    protected boolean checkKill(int color){
        //如果没将军则不判断
        if(!checkGeneral(color)){
            return false;
        }

        //对方的颜色
        int equalcolor = (color==ChessType.BLACK?ChessType.RED:ChessType.BLACK);

        ArrayList<ChessBean> chessBeanArrayList= getAllChess(equalcolor);
        for(ChessBean chess : chessBeanArrayList){

            //尝试将这个棋子走到它能走的每一步再判断是否能不被将军
            ArrayList<Point> points = getChessHint(chess);
            ChessBean chessCopy = copyChess(chess);

            for(Point point : points){
                //先临时保存棋子
                ChessBean tmpChess = copyChess(getChess(point));

                //移动棋子
                mChessData[point.x][point.y] =
                        new ChessBean(point,chessCopy.getType(),chessCopy.getColor());
                mChessData[chessCopy.getCoord().x][chessCopy.getCoord().y] = null;

                boolean isGeneral = checkGeneral(color);

                //恢复棋子!
                mChessData[chessCopy.getCoord().x][chessCopy.getCoord().y] = copyChess(chessCopy);
                mChessData[point.x][point.y] = copyChess(tmpChess);
                if(!isGeneral){
                    return false;
                }
            }
        }
        return true;

    }

    /**
     * 复制一个新的棋子
     * @param chess
     * @return
     */
    protected ChessBean copyChess(ChessBean chess){
        if(chess==null)return null;
        return new ChessBean(chess.getCoord(),chess.getType(),chess.getColor());
    }

    /**
     * 通过名字获取资源id
     *
     * @param imageName
     * @return
     */
    public int getResourceByName(String imageName) {
        int resId = getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        return resId;
    }

    /**
     * 将当前棋盘格式化成一段字符串
     *
     * @return
     */
    public String getStrData(){
//        //棋盘数据
//        protected ChessBean mChessData[][];
//        protected int mWinner;
//        protected boolean mGameOver;
//        protected int mCurColor; //当前轮到什么颜色下
//        protected Point mPreMovePoint[]; //记录上一次移动
//        protected int mChessCounter; //计步
        String result = "";
        ArrayList<ChessBean> chessList = getAllChess(-1);
        for(ChessBean chess : chessList){
            result += chess.toInt()+",";
        }
        result += (mGameOver?"1":"0")+",";
        result += mWinner+",";
        result += mCurColor+",";
        result += mPreMovePoint[0].x+","+mPreMovePoint[0].y+",";
        result += mPreMovePoint[1].x+","+mPreMovePoint[1].y+",";
        result += mChessCounter+",";
        result += mBottomColor;
        return result;
    }

    /**
     * 将当前棋盘数据设置为字符串中储存的数据
     * @param str
     */
    public void setStrData(String str){
        //分割
        String[] s = str.split(",");

        //设置棋子数据
        mChessData = new ChessBean[9][10]; //先清空
        int chessNum = s.length - 9;
        for(int i = 0; i<chessNum; i++){
            ChessBean chess = new ChessBean(Integer.valueOf(s[i]));
            mChessData[chess.getCoord().x][chess.getCoord().y] = chess;
        }

        //其他数据
        mGameOver = Integer.valueOf(s[chessNum])==1;
        mWinner = Integer.valueOf(s[chessNum+1]);
        mCurColor = Integer.valueOf(s[chessNum+2]);

        mPreMovePoint[0].x = Integer.valueOf(s[chessNum+3]);
        mPreMovePoint[0].y = Integer.valueOf(s[chessNum+4]);
        mPreMovePoint[1].x = Integer.valueOf(s[chessNum+5]);
        mPreMovePoint[1].y = Integer.valueOf(s[chessNum+6]);

        mChessCounter = Integer.valueOf(s[chessNum+7]);

        mBottomColor = Integer.valueOf(s[chessNum+8]);
        mTopColor = getOpponentColor(mBottomColor);
    }
}