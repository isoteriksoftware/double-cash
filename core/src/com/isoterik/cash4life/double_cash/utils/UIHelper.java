package com.isoterik.cash4life.double_cash.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.io.GameAssetsLoader;
import com.isoterik.mgdx.ui.ActorAnimation;

public final class UIHelper {
    public static TextureAtlas getAtlas() {
        return MinGdx.instance().assets.getAtlas(Constants.UI_ATLAS);
    }

    public static void showYourTurn(Stage canvas) {
        GameAssetsLoader assetsLoader = MinGdx.instance().assets;

        Image image = new Image(assetsLoader.regionForTexture("images/your_turn.png", true));
        image.setOrigin(image.getWidth()/2f, image.getHeight()/2f);
        ActorAnimation.instance().grow(image, .5f, Interpolation.swingOut);

        Table tbl = new Table();
        tbl.setFillParent(true);
        tbl.setBackground(((TextureRegionDrawable)assetsLoader.drawableForTexture("images/white.png"))
                .tint(new Color(0, 0, 0, .5f)));
        tbl.center();
        tbl.add(image);
        canvas.addActor(tbl);

    }

    public static void showOpponentTurn(Stage canvas) {
        GameAssetsLoader assetsLoader = MinGdx.instance().assets;

        Image image = new Image(assetsLoader.regionForTexture("images/opponent_turn.png", true));
        image.setOrigin(image.getWidth()/2f, image.getHeight()/2f);
        ActorAnimation.instance().grow(image, .5f, Interpolation.swingOut);

        Table tbl = new Table();
        tbl.setFillParent(true);
        tbl.setBackground(((TextureRegionDrawable)assetsLoader.drawableForTexture("images/white.png"))
                .tint(new Color(0, 0, 0, .5f)));
        tbl.center();
        tbl.add(image);
        canvas.addActor(tbl);

    }

    public static Window newWindow() {
        GameAssetsLoader assetsLoader = MinGdx.instance().assets;

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = new BitmapFont();
        style.stageBackground = ((TextureRegionDrawable)assetsLoader.drawableForTexture("images/white.png"))
                .tint(new Color(0, 0, 0, .5f));
        style.background = new NinePatchDrawable(getAtlas().createPatch("window"));

        Window window = new Window("", style);
        window.setKeepWithinStage(false);
        window.setModal(true);

        return window;
    }

    public static void showStakeDialog(Stage canvas) {
        Window window = newWindow();
        window.setSize(700, 400);
        canvas.addActor(window);

        centerActor(window, canvas);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, 1f, Interpolation.swingOut);
    }

    public static void centerActor(Actor actor, Stage canvas) {
        actor.setX((canvas.getWidth() - actor.getWidth())/2f);
        actor.setY((canvas.getHeight() - actor.getHeight())/2f);
    }
}

























