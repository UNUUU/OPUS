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

import com.squareup.otto.Subscribe;
import com.unuuu.opus.event.BusHolder;
import com.unuuu.opus.event.ChangeFlashModeEvent;
import com.unuuu.opus.event.SavedImageEvent;
import com.unuuu.opus.util.FileUtil;
import com.unuuu.opus.util.LogUtil;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    
    private Camera camera;
    private SurfaceHolder holder;

    private final static float SURFACE_VIEW_SIZE = 278.0f;

    public CameraPreview(Context context) {
        super(context);

        holder = getHolder();
        holder.addCallback(this);

        setDrawingCacheEnabled(true);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        Bitmap currentBitmap = get();
        int w = currentBitmap.getWidth();
        int h = currentBitmap.getHeight();

        Bitmap baseBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        // DST
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseCanvas.drawBitmap(currentBitmap, 0, 0, paint);

        // SRC
        Bitmap circleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas circleCanvas = new Canvas(circleBitmap);
        float radius;
        if (w > h) {
            radius = h / 2;
        } else {
            radius = w / 2;
        }
        float density = getContext().getResources().getDisplayMetrics().density;
        // ファインダーの画像が真ん中ではないので少し上に調整
        circleCanvas.drawCircle(w / 2, (h - (8 * density)) / 2, radius, paint);
        baseCanvas.drawBitmap(circleBitmap, 0, 0, paint);

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

        // TODO:: なぜか透明になる部分とは逆が消える
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawBitmap(baseBitmap, 0, 0, paint2);

        canvas.restore();
    }

    private Bitmap get() {
        return getDrawingCache();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            float width = getWidth();
            float height = getHeight();

            // 短い方のサイズに合わせる
            float density = getContext().getResources().getDisplayMetrics().density;

            float scale;
            if (width > height) {
                scale = (SURFACE_VIEW_SIZE * density) / height;
            } else {
                scale = (SURFACE_VIEW_SIZE * density) / width;
            }

            getHolder().setFixedSize((int) (width * scale), (int) (height * scale));

            setWillNotDraw(false);

            camera = Camera.open();
            camera.setDisplayOrientation(90);
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
        try {
            camera.startPreview();
        } catch (RuntimeException e) {
            LogUtil.e(e);
        }
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
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /**
     * シャッターを切る
     */
    public void takePicture() {
        if (camera == null) {
            return;
        }
        // 写真を撮影する
        camera.takePicture(mShutterCallback, null, mPictureListener);
    }

    // シャッターがきられる時に呼ばれるコールバック
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    // 撮影後に呼ばれるコールバック
    private Camera.PictureCallback mPictureListener = new Camera.PictureCallback() {
        // データ生成完了
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                // TODO:: 写真のデータを取得できない時の処理
                return;
            }

            // 縦の撮影では90度回転してしまっているので正しい角度に直す
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(
                    data, 0, data.length);
            int rotatedWidth = originalBitmap.getHeight();
            int rotatedHeight = originalBitmap.getWidth();
            Bitmap rotatedBitmap = Bitmap.createBitmap(rotatedWidth, rotatedHeight, Bitmap.Config.ARGB_8888);
            {
                Canvas canvas = new Canvas(rotatedBitmap);
                canvas.save();

                canvas.rotate(90, rotatedWidth / 2, rotatedHeight / 2);
                int offset = (rotatedHeight - rotatedWidth) / 2 * ((90 - 180) % 180) / 90;
                canvas.translate(offset, -offset);
                canvas.drawBitmap(originalBitmap, 0, 0, null);
                canvas.restore();
                originalBitmap.recycle();
            }

            // 正方形にして画像を丸に切り抜く
            // 正方形の1辺の長さをもとめる
            int oneSide;
            if (rotatedWidth > rotatedHeight) {
                oneSide = rotatedHeight;
            } else {
                oneSide = rotatedWidth;
            }

            Bitmap rounderBitmap = Bitmap.createBitmap(oneSide, oneSide, Bitmap.Config.ARGB_8888);
            {
                Canvas canvas = new Canvas(rounderBitmap);
                canvas.save();

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                // DST
                // 画像を正方形にするためにX, Yをずらして表示する
                float x = 0;
                float y = 0;
                if (rotatedWidth > rotatedHeight) {
                    x = -1 * (rotatedWidth - rotatedHeight) / 2;
                } else {
                    y = -1 * (rotatedHeight - rotatedWidth) / 2;
                }
                canvas.drawBitmap(rotatedBitmap, x, y, paint);

                Bitmap circleBitmap = Bitmap.createBitmap(oneSide, oneSide, Bitmap.Config.ARGB_8888);
                Canvas circleCanvas = new Canvas(circleBitmap);

                // 丸を描く
                circleCanvas.drawCircle(oneSide / 2, oneSide / 2, oneSide / 2, paint);

                // SRC (DSTとSRCが重なっている所のDSTをとる)
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

                canvas.drawBitmap(circleBitmap, 0, 0, paint);
                canvas.restore();
                rotatedBitmap.recycle();
                circleBitmap.recycle();
            }
            String imagePath = FileUtil.writeToDownloadDirectory(getContext(), rounderBitmap, "png");
            rounderBitmap.recycle();

            // 画像の保存完了
            BusHolder.get().post(new SavedImageEvent(imagePath));
        }
    };

    /**
     * フラッシュモードを切り替える
     * @param isFlashMode フラッシュをONにするかどうか
     */
    private void setFlashMode(boolean isFlashMode) {
        Camera.Parameters parameters = camera.getParameters();
        if (isFlashMode) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(parameters);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void subscribe(ChangeFlashModeEvent event) {
        setFlashMode(event.isFlashMode());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusHolder.get().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        BusHolder.get().unregister(this);
        super.onDetachedFromWindow();
    }
}