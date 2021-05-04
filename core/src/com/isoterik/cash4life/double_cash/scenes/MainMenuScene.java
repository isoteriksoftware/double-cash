package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;

public class MainMenuScene extends Scene {
    private MinGdx minGdx;

    public MainMenuScene() {
        minGdx = MinGdx.instance();
        setupCamera();

        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));

        GameObject bg = newSpriteObject(minGdx.assets.regionForTexture("images/background_red.png"));
        addGameObject(bg);
    }

    private void setupCamera() {
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));
        inputManager.getInputMultiplexer().addProcessor(canvas);
    }
}





























