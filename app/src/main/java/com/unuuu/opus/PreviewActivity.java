package com.unuuu.opus;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private String imagePath;

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
     * @param activity アクティビティ
     * @param imagePath 画像のパス
     */
    public static Intent getCallingIntent(@NonNull Activity activity, @NonNull String imagePath) {
        return new Intent(activity, PreviewActivity.class)
                .putExtra(KEY_IMAGE_PATH, imagePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        imagePath = getIntent().getStringExtra(KEY_IMAGE_PATH);

        LogUtil.d("画像のパス: " + imagePath);

        ButterKnife.bind(this);

        // 画像を読み込む
        Picasso.with(getApplicationContext()).load(new File(imagePath)).fit().noFade().into(frameView);

        dateView.setText(getImageCreatedAt());

        shareButton.setOnClickListener(v -> shareImage(imagePath));

        deleteButton.setOnClickListener(v -> {
            FileUtil.removeFile(imagePath);
            finish();
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
}
