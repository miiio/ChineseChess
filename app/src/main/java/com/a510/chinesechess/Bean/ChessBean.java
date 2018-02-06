package com.a510.chinesechess.Bean;

import android.graphics.Point;

/**
 * Created by Lao on 2018/1/6.
 */

public class ChessBean {
    public ChessBean(Point coord, int type, int color) {
        Coord = new Point(coord.x,coord.y);
        Type = type;
        Color = color;
        isAlive = true;
    }

    /**
     * 通过一个12位二进制构造一个棋子(0000 0000 0000 后8位分别是coord.x,coord.y 9~11存type, 12存color)
     * @param i
     */
    public ChessBean(int i) {
        setValue(i);
    }

    public void setValue(int i){
        this.Color = i>>11&1; //第12位为
        this.Type = i>>8&7;
        if(this.Coord == null){
            this.Coord = new Point();
        }
        this.Coord .set(i&15,i>>4&15);
        isAlive = true;
    }

    private Point Coord;
    private int Type;
    private int Color;

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    private boolean isAlive;

    public Point getCoord() {
        return Coord;
    }

    public void setCoord(Point coord) {
        Coord.set(coord.x,coord.y);
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    /**
     * 将棋子用12位二进制储存(0000 0000 0000 1~8位分别是coord.x,coord.y 9~11存type, 12存color)
     * @return
     */
    public int toInt() {
        int result = Coord.x;
        result |= Coord.y<<4; //将y左移4位后存入result
        result |= Type<<8;
        result |= Color<<11;
        return result;
    }
}
