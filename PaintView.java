package com.example.draw_and_guess_naor_shamsian;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.Nullable;


class PaintView extends View {
    public static int BRUSH_SIZE = 10;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;

    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public ArrayList<Draw> paths = new ArrayList<>();
    public ArrayList<Draw> undo = new ArrayList<>();
    public boolean canDraw;

    public PaintView(Context context, DisplayMetrics displayMetrics) {

        super(context, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        canDraw = true;
        initialise(displayMetrics);
    }

    public PaintView(Context context, AttributeSet attrs) {

        super(context, attrs);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void initialise(DisplayMetrics displayMetrics) {

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        mCanvas.drawColor(backgroundColor); // WRONG

        for (Draw draw : paths) {
            if (mPaint != null) {
                mPaint.setColor(draw.color); // WRONG
                mPaint.setStrokeWidth(draw.strokeWidth);
                mPaint.setMaskFilter(null);

                mCanvas.drawPath(draw.path, mPaint);
            }

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();

    }

    private void touchStart(float x, float y) {

        mPath = new Path();

        Draw draw = new Draw(currentColor, strokeWidth, mPath);
        paths.add(draw);

        mPath.reset();
        mPath.moveTo(x, y);

        mX = x;
        mY = y;

    }

    private void touchMove(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

            mX = x;
            mY = y;

        }

    }

    private void touchUp() {

        mPath.lineTo(mX, mY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canDraw) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    invalidate();
                    break;

            }

            return true;
        }
        return false;

    }

    public void clear() {

        backgroundColor = DEFAULT_BG_COLOR;

        paths.clear();
        invalidate();

    }

    public void undo() {

        if (paths.size() > 0) {

            undo.add(paths.remove(paths.size() - 1));
            invalidate(); // add

        } else {

            Toast.makeText(getContext(), "Nothing to undo", Toast.LENGTH_LONG).show();

        }

    }

    public void redo() {

        if (undo.size() > 0) {

            paths.add(undo.remove(undo.size() - 1));
            invalidate(); // add

        } else {

            Toast.makeText(getContext(), "Nothing to redo", Toast.LENGTH_LONG).show();

        }

    }

    public void setStrokeWidth(int width) {

        strokeWidth = width;

    }

    public void setColor(int color) {

        currentColor = color;

    }

}
