package com.unuuu.opus.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private Camera mCamera;
    private final SurfaceHolder mHolder;
    private Bitmap mRounderBitmap;
    private Paint mMaskPaint;
    private boolean mIsAdjustHeight;

    // Surfaceの大きさ
    private final static float SURFACE_VIEW_WIDTH = 278.0f;
    private final static float SURFACE_VIEW_HEIGHT = 278.0f;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        this.mCamera = camera;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);

        this.setDrawingCacheEnabled(true);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap currentBitmap = get();
        int w = currentBitmap.getWidth();
        int h = currentBitmap.getHeight();

        if (this.mRounderBitmap == null) {
            this.mRounderBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        Canvas c = new Canvas(this.mRounderBitmap);

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
        canvas.drawBitmap(this.mRounderBitmap, 0, 0, null);
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
            this.mCamera.setPreviewDisplay(holder);
            this.mCamera.startPreview();
        } catch (IOException e) {
            LogUtil.d("Error setting camera preview : " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mCamera.stopPreview();
        this.mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = this.mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(width, height);
        parameters.setPreviewSize(size.width, size.height);
        this.mCamera.setParameters(parameters);
        this.mCamera.startPreview();
    }

    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        Camera.Parameters p = this.mCamera.getParameters();
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
        this.mCamera.release();
        this.mCamera = null;
    }

    /**
     * シャッターを切る
     */
    public void takePicture() {
        if (this.mCamera == null) {
            return;
        }
        // 写真を撮影する
        this.mCamera.takePicture(null, null, mPictureListener);
    }

    // JPEGイメージ生成後に呼ばれるコールバック
    private Camera.PictureCallback mPictureListener = new Camera.PictureCallback() {
        // データ生成完了
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                // TODO:: 写真のデータを取得できない時の処理
                return;
            }

            // 縦で撮影すると90度回転して画像が取得できるのでちゃんとする
//            Bitmap originalBitmap = BitmapFactory.decodeByteArray(
//                    data, 0, data.length);
//            int rotatedWidth = originalBitmap.getHeight();
//            int rotatedHeight = originalBitmap.getWidth();
//            Bitmap rotatedBitmap = Bitmap.createBitmap(rotatedWidth, rotatedHeight, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(rotatedBitmap);
//            canvas.save();
//
//            canvas.rotate(90, rotatedWidth / 2, rotatedHeight / 2);
//            int offset = (rotatedHeight - rotatedWidth) / 2 * ((90 - 180) % 180) / 90;
//            canvas.translate(offset, -offset);
//            canvas.drawBitmap(originalBitmap, 0, 0, null);
//            canvas.restore();
//            originalBitmap.recycle();

//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+ "/camera_test.jpg");
//                fos.write(data);
//                fos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    };
}