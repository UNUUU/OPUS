package com.unuuu.opus.component;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.unuuu.opus.R;

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


}
