package com.unuuu.opus.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;
import com.unuuu.opus.PreviewActivity;
import com.unuuu.opus.R;
import com.unuuu.opus.event.BusHolder;
import com.unuuu.opus.event.SavedImageEvent;
import com.unuuu.opus.event.TakePictureEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kashima on 15/06/15.
 */
public class MainFragment extends Fragment {
    /** カメラ周りの処理をする */
    CameraPreview mCameraPreview;

    @Bind(R.id.fragment_main_layout_002)
    FrameLayout mCameraLayout;

    // View作成
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        mCameraPreview = new CameraPreview(getActivity().getApplicationContext());
        mCameraLayout.addView(mCameraPreview);

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

    @SuppressWarnings("unused")
    @Subscribe
    public void subscribe(SavedImageEvent event) {
        PreviewActivity.startActivity(getActivity(), event.mImagePath);
    }
}
