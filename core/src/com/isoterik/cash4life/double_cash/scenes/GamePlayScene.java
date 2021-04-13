package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.isoterik.cash4life.double_cash.components.Card;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;

public class GamePlayScene extends Scene {
    private MinGdx minGdx;
    
    private Array<GameObject> cards;
    
    public GamePlayScene() {
        minGdx = MinGdx.instance();
        
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
}




























