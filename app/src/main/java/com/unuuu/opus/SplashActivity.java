package com.unuuu.opus;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;

import permissions.dispatcher.DeniedPermissions;
import permissions.dispatcher.NeedsPermissions;
import permissions.dispatcher.RuntimePermissions;

/**
 *
 */
@RuntimePermissions
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // パーミッションチェックをする
        checkPermissions();
    }

    /**
     * パーミッションチェックをする
     */
    private void checkPermissions() {
        SplashActivityPermissionsDispatcher.checkPermissionsCameraAndStorageWithCheck(this);
    }

    /**
     * 外部ストレージとカメラのパーミッションチェックをする
     */
    @NeedsPermissions({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    void checkPermissionsCameraAndStorage() {
        MainActivity.startActivity(this);
        finish();
    }

    /**
     * パーミッションが拒否された時に呼ばれる
     */
    @DeniedPermissions({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    void showDeniedForPermissionsCameraAndStorage() {
        // アプリを終了する
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        SplashActivityPermissionsDispatcher.
                onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
