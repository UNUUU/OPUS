package com.unuuu.opus.event;

import android.support.annotation.NonNull;

/**
 *
 */
public class SavedImageEvent {
    private String imagePath;

    public SavedImageEvent(@NonNull String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
