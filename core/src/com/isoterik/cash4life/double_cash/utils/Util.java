package com.isoterik.cash4life.double_cash.utils;

import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.audio.AudioManager;

public class Util {
    public static void playClickSound() {
        AudioManager.instance().playSound(MinGdx.instance().assets.getSound("sfx/click.ogg"), 1f);
    }
}
