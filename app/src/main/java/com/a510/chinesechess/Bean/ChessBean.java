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
    }

    private Point Coord;
    private int Type;
    private int Color;

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
}
