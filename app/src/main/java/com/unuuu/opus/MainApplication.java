package com.unuuu.opus;

import android.app.Application;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.unuuu.opus.di.AppComponent;
import com.unuuu.opus.di.AppModule;
import com.unuuu.opus.di.DaggerAppComponent;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

public class MainApplication extends Application {
    private AppComponent appComponent;

    @Inject
    public OkHttpClient httpClient;

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

        new StethoWrapper(this).setup();

        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(httpClient));
    }
}
