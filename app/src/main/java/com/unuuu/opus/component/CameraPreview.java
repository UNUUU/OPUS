package com.unuuu.opus.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;
    private final SurfaceHolder holder;
    private Bitmap rounderBitmap;
    private Paint xferPaint;

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

        if (xferPaint == null) {
            xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        xferPaint.setColor(Color.RED);

        c.drawCircle(w / 2, h / 2, h / 2, xferPaint);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        canvas.drawBitmap(currentBitmap, 0, 0, xferPaint);
        canvas.drawBitmap(rounderBitmap, 0, 0, null);
    }

    private Bitmap get() {
        return this.getDrawingCache();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            setWillNotDraw(false);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            // Log.d("Error setting camera preview : %s", e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = previewSizes.get(0);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);

        // プレビュー開始
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
        camera = null;
    }
}