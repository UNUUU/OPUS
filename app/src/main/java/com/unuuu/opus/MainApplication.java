package com.unuuu.opus;

import android.app.Application;
import android.support.annotation.NonNull;

import com.unuuu.opus.di.AppComponent;
import com.unuuu.opus.di.AppModule;
import com.unuuu.opus.di.DaggerAppComponent;

public class MainApplication extends Application {
    private AppComponent appComponent;

    @NonNull
    public AppComponent getComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
