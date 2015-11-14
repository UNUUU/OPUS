package com.unuuu.opus.component;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kashima on 15/06/15.
 */
public class CircleCameraFragment extends Fragment {

    @Bind(R.id.fragment_circle_camera_layout_001)
    FrameLayout mRootLayout;

    /** カメラ周りの処理をする */
    CameraPreview mCameraPreview;

    // View作成
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_circle_camera, container, false);

        ButterKnife.bind(this, rootView);

        mCameraPreview = new CameraPreview(getActivity().getApplicationContext());
        mRootLayout.addView(this.mCameraPreview);

        return rootView;
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

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);

        super.onDestroyView();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void subscribe(TakePictureEvent event) {
        // 写真撮影をする
        this.mCameraPreview.takePicture();
    }
}
