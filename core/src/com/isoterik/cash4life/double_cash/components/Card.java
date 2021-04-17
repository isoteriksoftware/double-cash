package com.isoterik.cash4life.double_cash.components;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.isoterik.mgdx.Component;
import com.isoterik.mgdx.m2d.components.SpriteRenderer;

public class Card extends Component {
    public static float HIDDEN_SCALE = 0.5f;

    public final int number;

    public final TextureRegion cardSprite, cardBackSprite;

    public Card(TextureAtlas.AtlasRegion region, TextureRegion cardBackSprite) {
        this.cardBackSprite = cardBackSprite;
        this.cardSprite = region;
        this.number = Integer.parseInt(region.name.replaceAll("[^0-9]", ""));
    }

    @Override
    public void attach() {
        setRevealed(false);
    }

    public void setRevealed(boolean isRevealed) {
        SpriteRenderer spriteRenderer = getComponent(SpriteRenderer.class);

        if (isRevealed) {
            spriteRenderer.setSprite(cardSprite);
            gameObject.transform.setScale(1, 1);
        }
        else{
            spriteRenderer.setSprite(cardBackSprite);
            gameObject.transform.setScale(HIDDEN_SCALE, HIDDEN_SCALE);
        }
    }
}




























