package com.unuuu.opus.event;

/**
 *
 */
public class FlashModeChangedEvent {
    private boolean isFlashMode;

    public FlashModeChangedEvent(boolean isFlashMode) {
        this.isFlashMode = isFlashMode;
    }

    public boolean isFlashMode() {
        return isFlashMode;
    }
}
