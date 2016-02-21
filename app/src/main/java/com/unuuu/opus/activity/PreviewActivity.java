package com.unuuu.opus.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unuuu.opus.R;
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

    private static final String KEY_PICTURE_PATH = "picturePath";

    @Bind(R.id.activity_preview_image_frame)
    ImageView frameView;

    @Bind(R.id.activity_preview_text_date)
    TextView dateView;

    @Bind(R.id.activity_preview_image_share)
    ImageView shareButton;

    @Bind(R.id.activity_preview_image_delete)
    ImageView deleteButton;

    /**
     * 画像のパスを指定してアクティビティを起動する
     *
     * @param activity  アクティビティ
     * @param imagePath 画像のパス
     */
    public static Intent getCallingIntent(@NonNull Activity activity, @NonNull String imagePath) {
        return new Intent(activity, PreviewActivity.class)
                .putExtra(KEY_PICTURE_PATH, imagePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        String picturePath = getIntent().getStringExtra(KEY_PICTURE_PATH);
        LogUtil.d("画像のパス: " + picturePath);
        setPicture(picturePath);
        setPictureCreatedAt();

        shareButton.setOnClickListener(v -> sharePicture(picturePath));
        deleteButton.setOnClickListener(v -> deletePicture(picturePath));
    }

    /**
     * 写真を設定する
     *
     * @param picturePath 写真のパス
     */
    private void setPicture(String picturePath) {
        Glide.with(this).load(new File(picturePath)).fitCenter().into(frameView);
    }

    /**
     * 写真を削除する
     *
     * @param picturePath 写真のパス
     */
    private void deletePicture(@NonNull String picturePath) {
        FileUtil.removeFile(picturePath);
        finish();
    }

    /**
     * 写真を共有する
     *
     * @param picturePath 写真のパス
     */
    private void sharePicture(@NonNull String picturePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(picturePath)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtil.e("画像の共有に失敗しました: " + e.toString());
        }
    }

    /**
     * 写真の作成日を設定する
     */
    private void setPictureCreatedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        String month = sdf.format(Calendar.getInstance().getTime());
        sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String year = sdf.format(Calendar.getInstance().getTime());
        dateView.setText(String.format("on %s, %s", month, year));
    }
}
