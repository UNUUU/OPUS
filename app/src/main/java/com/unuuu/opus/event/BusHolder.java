package com.unuuu.opus.event;

import com.squareup.otto.Bus;

/**
 * 各イベントの橋渡し
 */
public class BusHolder {
    private static Bus sBus = new Bus();

    public static Bus get() {
        return sBus;
    }
}