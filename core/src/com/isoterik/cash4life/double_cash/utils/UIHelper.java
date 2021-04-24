package com.isoterik.cash4life.double_cash.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.io.GameAssetsLoader;
import com.isoterik.mgdx.ui.ActorAnimation;

public final class UIHelper {
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
}

























