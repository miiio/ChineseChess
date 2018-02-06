package com.a510.chinesechess.Util;

import android.graphics.Point;

import com.a510.chinesechess.Bean.ChessBean;
import com.a510.chinesechess.View.ChessBoardView;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Lao on 2018/2/6.
 */

public class ChessAIUtil {

    private ChessBean mChessData[][];
    private int mBottomColor; //位于下方棋子的颜色
    private int mTopColor; //位于上方
    private ArrayList<ChessBean> mPieces[];
    private int mSearchCnt = 0;

    public ChessAIUtil(String str){
        //分割
        String[] s = str.split(",");
        //设置棋子数据
        mChessData = new ChessBean[9][10];
        mPieces = new ArrayList[2];
        mPieces[0] = new ArrayList<>();
        mPieces[1] = new ArrayList<>();
        int chessNum = s.length - 9;
        for(int i = 0; i<chessNum; i++){
            ChessBean chess = new ChessBean(Integer.valueOf(s[i]));
            mChessData[chess.getCoord().x][chess.getCoord().y] = chess;
            mPieces[chess.getColor()].add(chess);
        }
        mBottomColor = Integer.valueOf(s[chessNum+8]);
        mTopColor = getOpponentColor(mBottomColor);
    }

    private int evaluate(int color){
        int val = 0;
        for(ChessBean chess : mPieces[color]){
            if(!chess.isAlive())continue;
            if(color == mBottomColor){
                val += ChessValue.Value[chess.getType()][chess.getCoord().y][chess.getCoord().x];
            }else{
                val += ChessValue.Value[chess.getType()][9-chess.getCoord().y][chess.getCoord().x];
            }
        }
        for(ChessBean chess : mPieces[getOpponentColor(color)]){
            if(!chess.isAlive())continue;
            if(getOpponentColor(color) == mBottomColor){
                val -= ChessValue.Value[chess.getType()][chess.getCoord().y][chess.getCoord().x];
            }else{
                val -= ChessValue.Value[chess.getType()][9-chess.getCoord().y][chess.getCoord().x];
            }
        }
        return val;
    }

    /**
     *
     * @param depth 搜索的深度，越深
     * @return
     */
    public BestSolve ai(int depth){
        BestSolve solve = new BestSolve();
        long timeCnt = new Date().getTime();
        mSearchCnt = 0;
        for(ChessBean chess : mPieces[mTopColor]) {
            if (!chess.isAlive()) continue;
            ArrayList<Point> points = ChessBoardView.getChessHint(chess, mChessData, mBottomColor);
            Point tmpPoint = new Point(chess.getCoord().x, chess.getCoord().y);
            for (Point point : points) {
                int nextPos = -1;
                int nextColor = -1;
                if (mChessData[point.x][point.y] != null) {
                    mChessData[point.x][point.y].setAlive(false);
                    nextColor = mChessData[point.x][point.y].getColor();
                    nextPos = mPieces[nextColor].indexOf(mChessData[point.x][point.y]);
                }
                mChessData[point.x][point.y] = chess;
                chess.setCoord(point);
                mChessData[tmpPoint.x][tmpPoint.y] = null;

                int val = -alphaBeta(depth - 1, mBottomColor, -99999, 99999);
                //int val = evaluate(mTopColor);
                //int val = minn(3);
                //int val = negaMax(3,mBottomColor);
                mChessData[tmpPoint.x][tmpPoint.y] = chess;
                chess.setCoord(tmpPoint);
                mChessData[point.x][point.y] = null;
                if (nextColor != -1) {
                    mChessData[point.x][point.y] = mPieces[nextColor].get(nextPos);
                    mChessData[point.x][point.y].setAlive(true);
                }

                if (val > solve.score) {
                    solve.score = val;
                    solve.chessPoint.set(tmpPoint.x, tmpPoint.y);
                    solve.point.set(point.x, point.y);
                }
            }
        }

        solve.time = (int) ((new Date().getTime()-timeCnt));
        solve.searchCnt = mSearchCnt;
        return solve;
    }
    private int alphaBeta(int depth, int color, int alpha, int beta){
        if(depth == 0){
            mSearchCnt++;
            return evaluate(color);
        }
        for(ChessBean chess : mPieces[color]){
            if(!chess.isAlive())continue;
            ArrayList<Point> points = ChessBoardView.getChessHint(chess,mChessData,mBottomColor);
            Point tmpPoint = new Point(chess.getCoord().x,chess.getCoord().y);
            for(Point point : points){
                int nextPos = -1;
                int nextColor = -1;
                if(mChessData[point.x][point.y] != null){
                    mChessData[point.x][point.y].setAlive(false);
                    nextColor = mChessData[point.x][point.y].getColor();
                    nextPos = mPieces[nextColor].indexOf(mChessData[point.x][point.y]);
                }
                mChessData[point.x][point.y] = chess;
                chess.setCoord(point);
                mChessData[tmpPoint.x][tmpPoint.y] = null;

                int val = -alphaBeta(depth - 1, getOpponentColor(color), -beta, -alpha);

                mChessData[tmpPoint.x][tmpPoint.y] = chess;
                chess.setCoord(tmpPoint);
                mChessData[point.x][point.y] = null;
                if(nextColor!=-1){
                    mChessData[point.x][point.y] = mPieces[nextColor].get(nextPos);
                    mChessData[point.x][point.y].setAlive(true);
                }
                if(val >= beta){
                    return beta;
                }
                if(val > alpha){
                    alpha = val;
                }
            }
        }
        return alpha;
    }
    /**
     * 获取相对颜色
     * @param color
     * @return
     */
    private int getOpponentColor(int color){
        return color == ChessType.RED?ChessType.BLACK:ChessType.RED;
    }
    public class BestSolve{
        public BestSolve() {
            chessPoint = new Point();
            point = new Point();
            time = 0;
            searchCnt = 0;
            score = -99999;
        }
        public Point chessPoint;
        public Point point;
        public int time;
        public int searchCnt;
        public int score;
    }

}
