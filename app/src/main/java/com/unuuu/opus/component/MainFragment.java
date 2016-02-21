package com.unuuu.opus.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.unuuu.opus.R;
import com.unuuu.opus.event.BusHolder;
import com.unuuu.opus.event.FlashModeChangedEvent;
import com.unuuu.opus.event.PictureTakenEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {
    /**
     * フラッシュが有効かどうか
     */
    private boolean isFlashOn = false;

    /**
     * カメラ周りの処理をする
     */
    private CameraPreview cameraPreview;

    @Bind(R.id.fragment_main_image_flash)
    ImageView flashButton;

    @Bind(R.id.fragment_main_layout_camera)
    FrameLayout cameraLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });

        return rootView;
    }

    /**
     * フラッシュを設定する
     */
    private void toggleFlash() {
        isFlashOn = !isFlashOn;

        if (isFlashOn) {
            flashButton.setImageResource(R.drawable.main_flash_on);
        } else {
            flashButton.setImageResource(R.drawable.main_flash_off);
        }

        BusHolder.get().post(new FlashModeChangedEvent(isFlashOn));
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Subscribe
    public void subscribe(@SuppressWarnings("unused") PictureTakenEvent event) {
        cameraPreview.takePicture();
    }
}
