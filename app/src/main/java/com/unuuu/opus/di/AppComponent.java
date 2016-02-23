package com.unuuu.opus.di;

import com.unuuu.opus.StethoWrapper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(StethoWrapper stethoDelegator);
}
