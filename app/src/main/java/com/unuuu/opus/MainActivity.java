package com.unuuu.opus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.unuuu.opus.event.BusHolder;
import com.unuuu.opus.event.TakePictureEvent;
import com.unuuu.opus.util.LogUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.activity_main_frame_003)
    ImageButton mShutterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 写真を撮影するイベントを呼ぶ
                BusHolder.get().post(new TakePictureEvent());
            }
        });
    }

    @Override
    protected void onDestroy() {
        ButterKnife.reset(this);

        super.onDestroy();
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
}
