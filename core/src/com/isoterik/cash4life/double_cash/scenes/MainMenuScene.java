package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;

public class MainMenuScene extends Scene {
    private MinGdx minGdx;

    public MainMenuScene() {
        minGdx = MinGdx.instance();
        setupCamera();
    }

    private void setupCamera() {
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));
        inputManager.getInputMultiplexer().addProcessor(canvas);
    }
}





























