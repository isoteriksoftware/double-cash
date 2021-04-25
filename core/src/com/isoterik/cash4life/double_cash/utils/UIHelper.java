package com.isoterik.cash4life.double_cash.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.io.GameAssetsLoader;
import com.isoterik.mgdx.ui.ActorAnimation;

public final class UIHelper {
    private Stage canvas;
    private GameAssetsLoader assetsLoader;
    private Skin skin;

    public UIHelper(Stage canvas) {
        this.canvas = canvas;
        this.assetsLoader = MinGdx.instance().assets;
        this.skin = assetsLoader.getSkin(Constants.SKIN);
    }

    public void showYourTurn() {
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

    public void showOpponentTurn() {
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

    public Window newWindow() {
        Window window = new Window("", skin);
        window.setKeepWithinStage(false);
        window.setModal(true);

        return window;
    }

    public void showStakeDialog(Stage canvas) {
        Window window = newWindow();

        Label label = new Label("What's your stake?", skin);
        label.setFontScale(1.2f);
        label.setAlignment(Align.center);

        window.top().padTop(100);
        window.add(label).expandX().fillX();

        window.setSize(500, 400);
        canvas.addActor(window);
        centerActor(window, canvas);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, 1f, Interpolation.swingOut);
    }

    public static void centerActor(Actor actor, Stage canvas) {
        actor.setX((canvas.getWidth() - actor.getWidth())/2f);
        actor.setY((canvas.getHeight() - actor.getHeight())/2f);
    }
}

























