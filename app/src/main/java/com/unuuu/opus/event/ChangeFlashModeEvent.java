package com.unuuu.opus.event;

/**
 *
 */
public class ChangeFlashModeEvent {
    private boolean isFlashMode;

    public ChangeFlashModeEvent(boolean isFlashMode) {
        this.isFlashMode = isFlashMode;
    }

    public boolean isFlashMode() {
        return isFlashMode;
    }
}
