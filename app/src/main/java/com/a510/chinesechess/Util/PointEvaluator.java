package com.a510.chinesechess.Util;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * 一个point的平移动画
 *
 * Created by Lao on 2018/1/9.
 */

public class PointEvaluator implements TypeEvaluator<Point>{
    @Override
    public Point evaluate(float v, Point point, Point t1) {
        Point startPoint = point;
        Point endPoint = t1;

        int x = (int) (startPoint.x + v * (endPoint.x - startPoint.x));
        int y = (int) (startPoint.y + v * (endPoint.y - startPoint.y));
        return new Point(x, y);
    }
}
