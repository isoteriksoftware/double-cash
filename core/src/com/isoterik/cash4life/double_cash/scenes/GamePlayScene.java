package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.components.Card;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.utils.WorldUnits;

public class GamePlayScene extends Scene {
    private MinGdx minGdx;
    private WorldUnits worldUnits;

    private Array<GameObject> cards;
    
    public GamePlayScene() {
        minGdx = MinGdx.instance();
        setupCamera();

        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));

        cards = new Array<>();
        for (TextureAtlas.AtlasRegion region : minGdx.assets.getAtlas("spritesheets/cards.atlas")
                .getRegions()) {
            GameObject card = newSpriteObject("Card", region);
            card.addComponent(new Card(region.name));
            cards.add(card);
        }

        addGameObject(cards.get(6));
    }

    private void setupCamera() {
        worldUnits = new WorldUnits(Constants.GUI_WIDTH, Constants.GUI_HEIGHT, 100);
        float aspectRatio = (float) minGdx.graphics.getHeight() / (float) minGdx.graphics.getWidth();
        System.out.println(aspectRatio);

        mainCamera.setup(new ExtendViewport(worldUnits.getWorldWidth() * aspectRatio, worldUnits.getWorldHeight(),
                mainCamera.getCamera()), worldUnits);
    }

    @Override
    public void __resize(int width, int height) {
        super.__resize(width, height);

        float aspectRatio = (float) height / (float) width;
        System.out.println(aspectRatio);

        mainCamera.setup(new ExtendViewport(worldUnits.getWorldWidth() * aspectRatio, worldUnits.getWorldHeight(),
                mainCamera.getCamera()), worldUnits);
    }
}




























