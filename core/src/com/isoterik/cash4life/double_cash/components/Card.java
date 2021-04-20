package com.isoterik.cash4life.double_cash.components;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.isoterik.mgdx.Component;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.Transform;
import com.isoterik.mgdx.m2d.components.SpriteRenderer;

public class Card extends Component {
    public static float HIDDEN_SCALE = 0.7f;
    public static float GAME_OVER_SCALE = 0.7f;
    public static float OPPONENT_SELECTED_SCALE = 0.5f;

    public final int number;

    public final TextureRegion cardSprite, cardBackSprite;

    public Card(TextureAtlas.AtlasRegion region, TextureRegion cardBackSprite, Scene scene) {
        this.cardBackSprite = cardBackSprite;
        this.cardSprite = region;
        this.number = Integer.parseInt(region.name.replaceAll("[^0-9]", ""));
        this.scene = scene;
    }

    @Override
    public void attach() {
        super.attach();
        setRevealed(false);
    }

    public Vector2 getRealSize() {
        return scene.getMainCamera().getWorldUnits().toWorldUnit(cardSprite);
    }

    public void setRevealed(boolean isRevealed) {
        SpriteRenderer spriteRenderer = getComponent(SpriteRenderer.class);
        Vector2 realSize = getRealSize();

        if (isRevealed) {
            spriteRenderer.setSprite(cardSprite);
        }
        else{
            spriteRenderer.setSprite(cardBackSprite);
            realSize.scl(HIDDEN_SCALE);
        }
        gameObject.transform.setSize(realSize.x, realSize.y);
    }

    public void setGameOverRevealed() {
        SpriteRenderer spriteRenderer = getComponent(SpriteRenderer.class);
        spriteRenderer.setSprite(cardSprite);

        Vector2 realSize = getRealSize();
        realSize.scl(GAME_OVER_SCALE);
        gameObject.transform.setSize(realSize.x, realSize.y);
    }

    public void setOpponentSelected() {
        Vector2 realSize = scene.getMainCamera().getWorldUnits().toWorldUnit(cardSprite);
        realSize.scl(OPPONENT_SELECTED_SCALE);
        gameObject.transform.setSize(realSize.x, realSize.y);
    }

    public boolean isTouched(float touchX, float touchY) {
        Transform t = gameObject.transform;

        return touchX >= t.getX() && touchX <= t.getX() + getRealWidth() &&
                touchY >= t.getY() && touchY <= t.getY() + getRealHeight();
    }

    public float getRealWidth() {
        return gameObject.transform.getWidth() * gameObject.transform.getScaleX();
    }

    public float getRealHeight() {
        return gameObject.transform.getHeight() * gameObject.transform.getScaleY();
    }
}




























