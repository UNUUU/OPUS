package com.unuuu.opus.component;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;
import com.unuuu.opus.R;
import com.unuuu.opus.event.BusHolder;
import com.unuuu.opus.event.TakePictureEvent;

/**
 * Created by kashima on 15/06/15.
 */
public class CircleCameraFragment extends Fragment {
    View mRootView;
    CameraPreview mCameraPreview;

    // View作成
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // View作成
        this.mRootView = inflater.inflate(R.layout.fragment_circle_camera, container, false);

        Camera camera = Camera.open();
        camera.setDisplayOrientation(90);
        this.mCameraPreview = new CameraPreview(getActivity().getApplicationContext(), camera);

        FrameLayout layout = (FrameLayout)mRootView.findViewById(R.id.fragment_circle_camera_layout_001);
        layout.addView(this.mCameraPreview);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BusHolder.get().register(this);
    }

    @Override
    public void onPause() {
        BusHolder.get().unregister(this);

        super.onPause();
    }

    @Subscribe
    public void subscribe(TakePictureEvent event) {
        // カメラがない時
        if (this.mCameraPreview == null) {
            return;
        }
        this.mCameraPreview.takePicture();
    }
}
