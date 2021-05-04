package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
        setupUI();
    }

    private void setupCamera() {
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));
        inputManager.getInputMultiplexer().addProcessor(canvas);
    }

    private void setupUI() {
        Image bg = new Image(minGdx.assets.regionForTexture("images/background_red.png"));
        canvas.addActor(bg);

        Image logo = new Image(minGdx.assets.regionForTexture("images/logo.png", true));

        Table root = new Table();
        root.setFillParent(true);

        Skin skin = minGdx.assets.getSkin(Constants.SKIN);

        TextButton btnPlay = new TextButton("Play", skin, "large");
        Button btnHelp = new Button(skin, "help");
        Button btnSound = new Button(skin, "sound");
        Button btnQuit = new Button(skin, "quit");

        Table tbl = new Table();
        tbl.padBottom(50).padRight(50).padTop(150);

        tbl.add(btnPlay).expand().fillX().colspan(3);
        tbl.row();
        tbl.add(btnHelp);
        tbl.add(btnSound);
        tbl.add(btnQuit);

        root.left();
        root.add(logo).left().expandX().padLeft(50);
        root.add(tbl).expand().fill();

        canvas.addActor(root);
    }
}





























