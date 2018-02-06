package com.a510.chinesechess.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.a510.chinesechess.Util.ChessAIUtil;

/**
 * Created by Lao on 2018/2/6.
 */

public class PVEChessBoardView extends ChessBoardView{
    public void setAIFirst(boolean AIFirst) {
        this.AIFirst = AIFirst;
    }

    private boolean AIFirst; //默认玩家先手

    public PVEChessBoardView(Context context) {
        super(context);
    }

    public PVEChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PVEChessBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initChess() {
        super.initChess();
        if(AIFirst){
            mCurColor = mTopColor;
            onStep();
        }else{
            mCurColor = mBottomColor;
        }
    }

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
                                if(mCurColor==mBottomColor && mCurColor == getChess(chessPoint).getColor()) {
                                    mCheckChess.set(chessPoint.x, chessPoint.y);
                                }
                            }
                        }
                    }else{
                        mTouchPoint.set(-1,-1);
                        mTouched = false;
                    }

                    if(mUndoBtnRect.contains(point.x,point.y) && mCurColor==mBottomColor){
                        //悔棋
                        mUndoBtnStatus = 1;
                    }
                    if(mRestartBtnRect.contains(point.x,point.y) && mCurColor==mBottomColor){
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

    @Override
    void onStep() {
        if(mCurColor == mTopColor){
            //执行ai算法
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ChessAIUtil.BestSolve solve = new ChessAIUtil(getStrData()).ai(4);
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = solve;
                    mAiHandler.sendMessage(msg);
                }
            }).start();

        }
    }

    /**
     * UI线程
     */
    Handler mAiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                ChessAIUtil.BestSolve solve = (ChessAIUtil.BestSolve) msg.obj;
                moveChess(getChess(solve.chessPoint),solve.point,true);
                Toast.makeText(getContext(),"耗时:"+solve.time+"ms 分支:"
                        +solve.searchCnt+" 评分:"+solve.score,Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * 重新开始
     */
    public void restart(){
        initChess();
    }
}
