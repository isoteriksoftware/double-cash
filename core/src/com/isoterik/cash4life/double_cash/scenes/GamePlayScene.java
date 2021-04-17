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
import com.isoterik.mgdx.Transform;
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

        GameObject table = newSpriteObject(minGdx.assets.regionForTexture("images/table.png"));
        Transform tableTransform = table.transform;
        tableTransform.setPosition((worldUnits.getWorldWidth() - tableTransform.getWidth())/2f,
                worldUnits.toWorldUnit(10));
        addGameObject(table);

        GameObject opponent = newSpriteObject(minGdx.assets.regionForTexture("images/opponent.png"));
        Transform opponentTransform = opponent.transform;
        opponentTransform.setScale(1, 0.8f);
        opponentTransform.setPosition((tableTransform.getX() + tableTransform.getWidth()/2f - worldUnits.toWorldUnit(180)),
                tableTransform.getY() + tableTransform.getHeight() - worldUnits.toWorldUnit(95));
        addGameObject(opponent);

        cards = new Array<>();

        for (TextureAtlas.AtlasRegion region : minGdx.assets.getAtlas("spritesheets/cards.atlas")
                .getRegions()) {
            GameObject card = newSpriteObject("Card", region);
            card.addComponent(new Card(region.name));
            cards.add(card);
        }

        GameObject card = cards.get(6);
        placeInCenter(card, table);
        addGameObject(card);
    }

    private void setupCamera() {
        worldUnits = new WorldUnits(Constants.GUI_WIDTH, Constants.GUI_HEIGHT, 64);

        mainCamera.setup(new ExtendViewport(worldUnits.getWorldWidth(), worldUnits.getWorldHeight(),
                worldUnits.getWorldWidth(), worldUnits.getWorldHeight(),
                mainCamera.getCamera()), worldUnits);
    }

    private void placeInCenter(GameObject gameObject, GameObject host) {
        Transform gt = gameObject.transform;
        Transform ht = host.transform;

        gt.setPosition((ht.getX() + ht.getWidth()/2f) - gt.getWidth()/2f,
                (ht.getY() + ht.getHeight()/2f) - gt.getHeight()/2f);
    }
}




























