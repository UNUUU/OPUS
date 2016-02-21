package com.unuuu.opus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.unuuu.opus.event.BusHolder;
import com.unuuu.opus.event.SavedImageEvent;
import com.unuuu.opus.event.TakePictureEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    public static Intent getCallingIntent(@NonNull Activity activity) {
        return new Intent(activity, MainActivity.class);
    }

    @Bind(R.id.activity_main_image_shutter)
    ImageView shutterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 写真を撮影するイベントを呼ぶ
                BusHolder.get().post(new TakePictureEvent());
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void subscribe(SavedImageEvent event) {
        Intent intent = PreviewActivity.getCallingIntent(this, event.getImagePath());
        startActivity(intent);
    }
}
