package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;
import com.isoterik.mgdx.m2d.scenes.transition.TransitionDirection;
import com.isoterik.mgdx.ui.ActorAnimation;

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
        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toGamePlayScene();
            }
        });

        Button btnHelp = new Button(skin, "help");
        btnHelp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://cash4life.com.ng/double-cash/help");
            }
        });

        Button btnSound = new Button(skin, "sound");
        Button btnQuit = new Button(skin, "quit");
        btnQuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                minGdx.app.exit();
            }
        });

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

        ActorAnimation.instance().setup(canvas.getWidth(), canvas.getHeight());

        canvas.addActor(root);
    }

    private void toGamePlayScene() {
        minGdx.setScene(new GamePlayScene(), SceneTransitions.slide(1f, TransitionDirection.UP,
                true, Interpolation.pow5Out));
    }
}





























