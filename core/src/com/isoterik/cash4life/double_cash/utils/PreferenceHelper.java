package com.isoterik.cash4life.double_cash.utils;

import com.badlogic.gdx.Preferences;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.audio.AudioManager;

public final class PreferenceHelper {
    private final Preferences preferences;
    private static PreferenceHelper instance;

    private PreferenceHelper() {
        preferences = MinGdx.instance().app.getPreferences(Constants.PREFERENCES);
        AudioManager.instance().setSoundEnabled(isSoundEnabled());
    }

    public static void init() {
        instance = new PreferenceHelper();
    }

    public static PreferenceHelper instance() {
        return instance;
    }

    public boolean isSoundEnabled() {
        return preferences.getBoolean("soundEnabled", true);
    }

    public void setSoundEnabled(boolean enabled) {
        preferences.putBoolean("soundEnabled", enabled);
        AudioManager.instance().setSoundEnabled(enabled);
    }

    public void saveChanges() {
        MinGdx.instance().app.postRunnable(preferences::flush);
    }
}











