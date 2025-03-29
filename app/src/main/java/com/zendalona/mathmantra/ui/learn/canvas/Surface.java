package com.zendalona.mathmantra.ui.learn.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Surface extends View {

    private Paint paint;
    private Path path;
    private ArrayList<Path> pathList = new ArrayList<>();
    private ArrayList<Paint> paintList = new ArrayList<>();
    private Bitmap bitmap;
    private Canvas canvas;

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

        bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, null);

        if(!path.isEmpty()) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                invalidate();
                break;
        }
        invalidate();
        return true;
    }

    public void clearSurface() {
        bitmap.eraseColor(Color.WHITE);
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
