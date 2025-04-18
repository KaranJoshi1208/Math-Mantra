package com.zendalona.mathmantra.ui.learn.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Surface extends View {

    private static final int INVALID_POINTER_ID = -1;
    private static final float MAX_SCALE = 5.0f;
    private static final float MIN_SCALE = 0.1f;

    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private ScaleGestureDetector scaleGestureDetector;

    private float scaleFactor = 1f;
    private float moveX = 0f, moveY = 0f;
    private float lastX = 0f, lastY = 0f;
    private int activePointerId = INVALID_POINTER_ID;

    public Surface(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSurface();
    }

    private void initSurface() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        path = new Path();

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));
                invalidate();
                return true;
            }
        });
        // creation of canvas with fixed dimensions
//        bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(moveX, moveY);
        canvas.scale(scaleFactor, scaleFactor);

        canvas.drawBitmap(bitmap, 0, 0, null);
        if(!path.isEmpty()) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // creation of canvas with dynamic dimensions
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        final int action = event.getActionMasked();
        /**
         * Here pointerIndex is the index assigned to the ID of finger(touching the screen) in the list by current MotionEvent
         */
        final int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                activePointerId = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(activePointerId);
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                if(!scaleGestureDetector.isInProgress()) {
                    float dx = x - lastX;
                    float dy = y - lastY;

                    moveX += dx;
                    moveY += dy;

                    invalidate();
                }
                lastX = x;
                lastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastX = event.getX(newPointerIndex);
                    lastY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
        }
        return true;
    }

    public void clearSurface() {
        canvas.drawColor(Color.WHITE);
        path.reset();
        invalidate();
    }

    public Bitmap getBitmap() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }
}
