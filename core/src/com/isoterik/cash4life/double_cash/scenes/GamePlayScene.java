package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.components.Card;
import com.isoterik.mgdx.GameObject;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.Transform;
import com.isoterik.mgdx.input.ITouchListener;
import com.isoterik.mgdx.input.TouchEventData;
import com.isoterik.mgdx.input.TouchTrigger;
import com.isoterik.mgdx.m2d.components.debug.BoxDebugRenderer;
import com.isoterik.mgdx.utils.WorldUnits;

public class GamePlayScene extends Scene {
    private MinGdx minGdx;
    private WorldUnits worldUnits;

    private Array<GameObject> cards, pickedCards;
    private final TextureRegion cardBackSprite;

    private final GameObject table, opponent;
    private GameObject userChoice, opponentChoice;

    public GamePlayScene() {
        minGdx = MinGdx.instance();
        setupCamera();

        //setRenderCustomDebugLines(true);

        setBackgroundColor(new Color(.1f, .1f, .2f, 1f));

        GameObject bg = newSpriteObject(minGdx.assets.regionForTexture("images/background.png"));
        addGameObject(bg);

        table = newSpriteObject(minGdx.assets.regionForTexture("images/table.png"));
        Transform tableTransform = table.transform;
        tableTransform.setPosition((worldUnits.getWorldWidth() - tableTransform.getWidth())/2f,
                worldUnits.toWorldUnit(10));
        addGameObject(table);
        table.addComponent(new BoxDebugRenderer());

        opponent = newSpriteObject(minGdx.assets.regionForTexture("images/opponent.png"));
        Transform opponentTransform = opponent.transform;
        opponentTransform.setScale(1, 0.8f);
        opponentTransform.setPosition((tableTransform.getX() + tableTransform.getWidth()/2f - worldUnits.toWorldUnit(180)),
                tableTransform.getY() + tableTransform.getHeight() - worldUnits.toWorldUnit(100));
        addGameObject(opponent);

        cards = new Array<>();
        pickedCards = new Array<>();

        cardBackSprite = minGdx.assets.getAtlas("spritesheets/cards.atlas").findRegion("shirt_red");
        for (TextureAtlas.AtlasRegion region : minGdx.assets.getAtlas("spritesheets/cards.atlas")
                .getRegions()) {
            if (region.name.startsWith("shirt"))
                continue;

            GameObject card = newSpriteObject("Card", cardBackSprite);
            card.addComponent(new Card(region, cardBackSprite, this));
            cards.add(card);
            card.addComponent(new BoxDebugRenderer());
        }

        newGame();

        inputManager.addListener(TouchTrigger.touchDownTrigger(), new CardClickListener());
    }

    private void setupCamera() {
        worldUnits = new WorldUnits(Constants.GUI_WIDTH, Constants.GUI_HEIGHT, 64);

        mainCamera.setup(new ExtendViewport(worldUnits.getWorldWidth(), worldUnits.getWorldHeight(),
                worldUnits.getWorldWidth(), worldUnits.getWorldHeight(),
                mainCamera.getCamera()), worldUnits);
    }

    private void placeInCenterOf(GameObject gameObject, GameObject host) {
        Transform gt = gameObject.transform;
        Transform ht = host.transform;

        gt.setPosition((ht.getX() + ht.getWidth()/2f) - gt.getWidth()/2f,
                (ht.getY() + ht.getHeight()/2f) - gt.getHeight()/2f);
    }

    private void pickRandomCards() {
        cards.shuffle();
        pickedCards.clear();

        for (int i = 0; i < 5; i++)
            pickedCards.add(cards.get(i));
    }

    private void newGame() {
        placeCards();
    }

    private void placeCards() {
        pickRandomCards();

        // Calculate the position of the middle card
        GameObject middle = pickedCards.get(2);
        float mx = (table.transform.getX() + table.transform.getWidth()/2f) - middle.transform.getWidth()/2f;
        float my = (table.transform.getY() + table.transform.getHeight()/2f) - middle.transform.getHeight()/2f;
        middle.transform.setPosition(mx, my);
        addGameObject(middle);

        float spacing = worldUnits.toWorldUnit(10);

        for (int i = 0; i < 2; i++) {
            GameObject card = pickedCards.get(i);
            float t = 2 - i;
            float x = mx - (getRealWidth(card) * t) - spacing * t;
            card.transform.setPosition(x, my);
            addGameObject(card);
        }

        for (int i = 3; i < 5; i++) {
            GameObject card = pickedCards.get(i);
            float t = i - 2;
            float x = mx + (getRealWidth(card) * t) + spacing * t;
            card.transform.setPosition(x, my);
            addGameObject(card);
        }
    }

    private void cardSelected() {
        Card card = userChoice.getComponent(Card.class);
        card.setRevealed(true);
        placeInCenterOf(userChoice, table);
        userChoice.transform.position.y = worldUnits.toWorldUnit(10);

        pickedCards.removeValue(userChoice, true);
        playForOpponent();
    }

    private void playForOpponent() {
        opponentChoice = pickedCards.random();
        Transform ot = opponent.transform;
        Card card = opponentChoice.getComponent(Card.class);
        card.setOpponentSelected();

        opponentChoice.transform.setPosition(ot.getX(),
                ot.getY() - card.getRealHeight());

        pickedCards.removeValue(opponentChoice, true);
    }

    private GameObject getMaximumPick() {
        int max = 0;
        GameObject currentCard = pickedCards.first();
        for (GameObject card : pickedCards) {
            if (card.getComponent(Card.class).number > max)
                currentCard = card;
        }

        return currentCard;
    }

    private GameObject getMinimumPick() {
        int min = 0;
        GameObject currentCard = pickedCards.first();
        for (GameObject card : pickedCards) {
            if (card.getComponent(Card.class).number < min)
                currentCard = card;
        }

        return currentCard;
    }

    private float getRealWidth(GameObject gameObject) {
        return gameObject.transform.getWidth() * gameObject.transform.getScaleX();
    }

    private float getRealHeight(GameObject gameObject) {
        return gameObject.transform.getHeight() * gameObject.transform.getScaleY();
    }

    public class CardClickListener implements ITouchListener {
        @Override
        public void onTouch(String mappingName, TouchEventData touchEventData) {
            for (GameObject card : pickedCards) {
                if (card.getComponent(Card.class).isTouched(touchEventData.touchX, touchEventData.touchY)) {
                    userChoice = card;
                    cardSelected();
                    break;
                }
            }
        }
    }
}




























