package com.isoterik.cash4life.double_cash.utils;

import com.badlogic.gdx.Preferences;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.MinGdx;

public final class PreferenceHelper {
    private Preferences preferences;
    private static PreferenceHelper instance;

    private PreferenceHelper() {
        preferences = MinGdx.instance().app.getPreferences(Constants.PREFERENCES);
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
    }

    public void saveChanges() {
        MinGdx.instance().app.postRunnable(() -> preferences.flush());
    }
}











