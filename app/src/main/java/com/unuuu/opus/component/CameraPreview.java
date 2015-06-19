package com.unuuu.opus.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.unuuu.opus.util.LogUtil;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;
    private final SurfaceHolder holder;
    private Bitmap rounderBitmap;
    private Paint mMaskPaint;
    private boolean mIsAdjustHeight;

    // Surfaceの大きさ
    private final static float SURFACE_VIEW_WIDTH = 278.0f;
    private final static float SURFACE_VIEW_HEIGHT = 278.0f;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);

        this.setDrawingCacheEnabled(true);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap currentBitmap = get();
        int w = currentBitmap.getWidth();
        int h = currentBitmap.getHeight();

        if (rounderBitmap == null) {
            rounderBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        Canvas c = new Canvas(rounderBitmap);

        if (mMaskPaint == null) {
            mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        float radius;
        if (this.mIsAdjustHeight) {
            radius = h / 2;
        } else {
            radius = w / 2;
        }

        float density = getContext().getResources().getDisplayMetrics().density;
        // ファインダーの画像が真ん中ではないので少し上に調整
        c.drawCircle(w / 2, (h - (8 * density)) / 2, radius, mMaskPaint);

        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        canvas.drawBitmap(currentBitmap, 0, 0, mMaskPaint);
        canvas.drawBitmap(rounderBitmap, 0, 0, null);
    }

    private Bitmap get() {
        return this.getDrawingCache();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            float width = getWidth();
            float height = getHeight();

            // 短い方のサイズに合わせる
            float density = getContext().getResources().getDisplayMetrics().density;
            if (width > height) {
                this.mIsAdjustHeight = true;
            } else {
                this.mIsAdjustHeight = false;
            }

            float scale;
            if (this.mIsAdjustHeight) {
                scale = (SURFACE_VIEW_HEIGHT * density) / height;
            } else {
                scale = (SURFACE_VIEW_WIDTH * density) / width;
            }

            this.getHolder().setFixedSize((int)(width * scale), (int)(height * scale));

            setWillNotDraw(false);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            LogUtil.d("Error setting camera preview : " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = getBestPreviewSize(width, height);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        Camera.Parameters p = camera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
        camera = null;
    }
}