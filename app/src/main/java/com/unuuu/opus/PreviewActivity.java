package com.unuuu.opus;

import android.os.Bundle;

import butterknife.ButterKnife;

/**
 *
 */
public class PreviewActivity extends BaseActivity {
//    @Bind(R.id.activity_main_frame_003)
//    ImageButton mShutterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ButterKnife.bind(this);

//        mShutterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 写真を撮影するイベントを呼ぶ
//                BusHolder.get().post(new TakePictureEvent());
//            }
//        });
    }
}
