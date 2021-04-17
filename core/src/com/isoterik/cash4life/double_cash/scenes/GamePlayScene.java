package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.components.Card;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.m2d.components.SpriteRenderer;
import com.isoterik.mgdx.utils.WorldUnits;

public class GamePlayScene extends Scene {
    private MinGdx minGdx;
    private WorldUnits worldUnits;

    private Array<GameObject> cards;
    
    public GamePlayScene() {
        minGdx = MinGdx.instance();
        setupCamera();

        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));

        GameObject bg = newSpriteObject(minGdx.assets.regionForTexture("images/background.png"));
        addGameObject(bg);

        cards = new Array<>();

        for (TextureAtlas.AtlasRegion region : minGdx.assets.getAtlas("spritesheets/cards.atlas")
                .getRegions()) {
            GameObject card = newSpriteObject("Card", region);
            card.addComponent(new Card(region.name));
            cards.add(card);
        }

        GameObject card = cards.get(6);
        card.transform.setPosition((worldUnits.getWorldWidth() - card.transform.getWidth())/2f,
                (worldUnits.getWorldHeight() - card.transform.getHeight())/2f);
        addGameObject(card);
    }

    private void setupCamera() {
        worldUnits = new WorldUnits(Constants.GUI_WIDTH, Constants.GUI_HEIGHT, 64);

        mainCamera.setup(new ExtendViewport(worldUnits.getWorldWidth(), worldUnits.getWorldHeight(),
                worldUnits.getWorldWidth(), worldUnits.getWorldHeight(),
                mainCamera.getCamera()), worldUnits);
    }
}




























