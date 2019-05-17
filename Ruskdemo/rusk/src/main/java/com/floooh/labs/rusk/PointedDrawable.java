package com.floooh.labs.rusk;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author jayanthtg
 * draws arrow in the background at the given position.
 */

public class PointedDrawable extends Drawable {

    private Path mPath;
    private Paint mPaint;
    private Rect mRect;
    private int mPointHeight = 30;
    private int mPointWidth = 30;
    private Path mArrowPaths;
    private Rusk.Direction direction;
    private int position;
    private int from;

    public PointedDrawable(int color, Rusk.Direction direction, int position, int from) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);
        mArrowPaths = new Path();
        this.direction = direction;
        this.position = position;
        this.from = from;
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        switch (direction) {
            case TOP:
                bottom(bounds);
                break;
            case BOTTOM:
                top(bounds);
                break;
            default:
                bottomCenter(bounds);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mArrowPaths, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    //draw arrow at top center in the bg (not using)
    private void topCenter(Rect bounds) {
        mPath.reset();
        RectF rect = new RectF(bounds.left, bounds.top + mPointHeight, bounds.right, bounds.bottom - mPointHeight);
        mPath.addRect(rect, Path.Direction.CW);
        PointF a = new PointF(rect.centerX() - mPointWidth, rect.top);
        PointF b = new PointF(a.x + mPointWidth, rect.top - mPointHeight);
        PointF c = new PointF(b.x + mPointWidth, rect.top);
        mArrowPaths.moveTo(a.x, a.y);
        mArrowPaths.lineTo(b.x, b.y);
        mArrowPaths.lineTo(c.x, c.y);
    }

    //draw arrow at top for a given location
    private void top(Rect bounds) {
        mPath.reset();
        RectF rect = new RectF(bounds.left, bounds.top + mPointHeight, bounds.right, bounds.bottom - mPointHeight);
        mPath.addRect(rect, Path.Direction.CW);
        PointF a = new PointF(getPosition(new RectF(rect)) - mPointWidth, rect.top);
        PointF b = new PointF(a.x + mPointWidth, rect.top - mPointHeight);
        PointF c = new PointF(b.x + mPointWidth, rect.top);
        mArrowPaths.moveTo(a.x, a.y);
        mArrowPaths.lineTo(b.x, b.y);
        mArrowPaths.lineTo(c.x, c.y);
    }

    //draw arrow at bottom for a given location
    private void bottom(Rect bounds) {
        mPath.reset();
        RectF rect = new RectF(bounds.left, bounds.top + mPointHeight, bounds.right, bounds.bottom - mPointHeight);
        mPath.addRect(rect, Path.Direction.CW);
        PointF a = new PointF(getPosition(new RectF(rect)) - mPointWidth, rect.bottom);
        Log.d("Rusk", "from " + from);
        PointF b = new PointF(a.x + mPointWidth, rect.bottom + mPointHeight);
        PointF c = new PointF(b.x + mPointWidth, rect.bottom);
        mArrowPaths.moveTo(a.x, a.y);
        mArrowPaths.lineTo(b.x, b.y);
        mArrowPaths.lineTo(c.x, c.y);
    }

    private int getPosition(RectF bounds) {
        return position - from;
    }

    //draw arrow at bottom for a given location (not using)
    private void rbottomLocCenter(Rect bounds) {
        mPath.reset();
        RectF rect = new RectF(bounds.left, bounds.top + mPointHeight, bounds.right, bounds.bottom - mPointHeight);
        mPath.addRect(rect, Path.Direction.CW);

        PointF a = new PointF(rect.left + position, rect.bottom);
        PointF b = new PointF(a.x + mPointWidth, rect.bottom + mPointHeight);
        PointF c = new PointF(b.x + mPointWidth, rect.bottom);
        mArrowPaths.moveTo(a.x, a.y);
        mArrowPaths.lineTo(b.x, b.y);
        mArrowPaths.lineTo(c.x, c.y);
    }

    //draw arrow at bottom center in bg  (not using)
    private void bottomCenter(Rect bounds) {
        mPath.reset();
        RectF rect = new RectF(bounds.left, bounds.top + mPointHeight, bounds.right, bounds.bottom - mPointHeight);
        mPath.addRect(rect, Path.Direction.CW);
        PointF a = new PointF(rect.centerX() - mPointWidth, rect.bottom);
        PointF b = new PointF(a.x + mPointWidth, rect.bottom + mPointHeight);
        PointF c = new PointF(b.x + mPointWidth, rect.bottom);
        mArrowPaths.moveTo(a.x, a.y);
        mArrowPaths.lineTo(b.x, b.y);
        mArrowPaths.lineTo(c.x, c.y);
    }
}
