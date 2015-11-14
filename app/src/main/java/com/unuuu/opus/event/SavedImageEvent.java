package com.unuuu.opus.event;

import android.support.annotation.NonNull;

/**
 *
 */
public class SavedImageEvent {
    public String mImagePath;
    public SavedImageEvent(@NonNull String imagePath) {
        mImagePath = imagePath;
    }
}
