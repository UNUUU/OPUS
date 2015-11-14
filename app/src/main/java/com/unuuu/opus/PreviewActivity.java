package com.unuuu.opus;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.unuuu.opus.util.FileUtil;
import com.unuuu.opus.util.LogUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class PreviewActivity extends BaseActivity {

    private static final String KEY_IMAGE_PATH = "imagePath";

    /** 画像のパス */
    private String mImagePath;

    @Bind(R.id.activity_preview_frame_001)
    ImageView mImageView;

    @Bind(R.id.activity_preview_frame_002)
    TextView mDateView;

    @Bind(R.id.activity_preview_frame_005)
    ImageView mShareButton;

    @Bind(R.id.activity_preview_frame_006)
    ImageView mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mImagePath = getIntent().getStringExtra(KEY_IMAGE_PATH);

        LogUtil.d("画像のパス: " + mImagePath);

        ButterKnife.bind(this);

        // 画像を読み込む
        Picasso.with(getApplicationContext()).load(new File(mImagePath)).fit().noFade().into(mImageView);

        // 画像の撮影日を取得する
        mDateView.setText(getImageCreatedAt());

        // 画像を共有する
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(mImagePath);
            }
        });

        // 画像を削除する
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.removeFile(mImagePath);
                finish();
            }
        });
    }

    /**
     * 画像を共有する
     * @param imagePath 画像のパス
     */
    private void shareImage(String imagePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtil.e("画像の共有に失敗しました: " + e.toString());
        }
    }

    /**
     * 写真の作成日を取得する
     * @return 作成日
     */
    private String getImageCreatedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        String month = sdf.format(Calendar.getInstance().getTime());
        sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String year = sdf.format(Calendar.getInstance().getTime());
        return String.format("on %s, %s", month, year);
    }

    /**
     * 画像のパスを指定してアクティビティを起動する
     * @param context コンテキスト
     * @param imagePath 画像のパス
     */
    public static void startActivity(@NonNull Context context, @NonNull String imagePath) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(KEY_IMAGE_PATH, imagePath);
        context.startActivity(intent);
    }
}
