package com.unuuu.opus.event;

import android.support.annotation.NonNull;

/**
 *
 */
public class PictureSavedEvent {
    private String picturePath;

    public PictureSavedEvent(@NonNull String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }
}
