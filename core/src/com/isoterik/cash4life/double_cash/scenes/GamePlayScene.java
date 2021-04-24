package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.components.Card;
import com.isoterik.cash4life.double_cash.utils.UIHelper;
import com.isoterik.mgdx.*;
import com.isoterik.mgdx.input.ITouchListener;
import com.isoterik.mgdx.input.TouchEventData;
import com.isoterik.mgdx.input.TouchTrigger;
import com.isoterik.mgdx.m2d.components.debug.BoxDebugRenderer;
import com.isoterik.mgdx.ui.ActorAnimation;
import com.isoterik.mgdx.utils.WorldUnits;

public class GamePlayScene extends Scene {
    private MinGdx minGdx;
    private WorldUnits worldUnits;

    private Array<ActorGameObject> cards, pickedCards;
    private final TextureRegion cardBackSprite;

    private final GameObject table, opponent;
    private ActorGameObject userChoice, opponentChoice;

    private enum GameType { HIGHER, LOWER }
    private enum Turn { USER, OPPONENT }

    private GameType gameType = GameType.HIGHER;
    private Turn turn;
    private boolean canPlay = false;

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
        opponentTransform.setSize(opponentTransform.getWidth(), opponentTransform.getHeight() * 0.8f);
        opponentTransform.setPosition((tableTransform.getX() + tableTransform.getWidth()/2f - worldUnits.toWorldUnit(180)),
                tableTransform.getY() + tableTransform.getHeight() - worldUnits.toWorldUnit(70));
        addGameObject(opponent);

        cards = new Array<>();
        pickedCards = new Array<>();

        cardBackSprite = minGdx.assets.getAtlas("spritesheets/cards.atlas").findRegion("shirt_red");
        for (TextureAtlas.AtlasRegion region : minGdx.assets.getAtlas("spritesheets/cards.atlas")
                .getRegions()) {
            if (region.name.startsWith("shirt"))
                continue;

            ActorGameObject card = newActorSpriteObject("Card", cardBackSprite);

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
        setupAnimationCanvas(mainCamera.getViewport());

        //setupCanvas(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));
        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));
        ActorAnimation.instance().setup(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
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

        for (int i = 0; i < Constants.MAX_CARDS; i++)
            pickedCards.add(cards.get(i));

//        for (GameObject card : pickedCards)
//            System.out.print(card.getComponent(Card.class).number + " ");
    }

    private void newGame() {
        pickRandomCards();
        placeCards(false);

        UIHelper.showStakeDialog(canvas);

        if (MathUtils.randomBoolean())
            turn = Turn.USER;
        else
            turn = Turn.OPPONENT;
    }

    private void placeCards(boolean isGameOver) {
        int max = pickedCards.size;

        // Calculate the position of the middle card
        int middleIndex = max/2;
        ActorGameObject middle = pickedCards.get(middleIndex);
        float mx = (table.transform.getX() + table.transform.getWidth()/2f) - middle.transform.getWidth()/2f;
        float my = (table.transform.getY() + table.transform.getHeight()/2f) - middle.transform.getHeight()/2f;

        if (isGameOver)
            my -= worldUnits.toWorldUnit(30);

        middle.transform.setPosition(mx, my);
        addGameObject(middle);

        float spacing = worldUnits.toWorldUnit(10);

        for (int i = 0; i < middleIndex; i++) {
            ActorGameObject card = pickedCards.get(i);
            float t = middleIndex - i;
            float x = mx - (getRealWidth(card) * t) - spacing * t;

            card.actorTransform.actor.addAction(Actions.moveTo(x, my,
                    1f, Interpolation.pow5Out));

            card.transform.setPosition(mx, my);
            addGameObject(card);
        }

        for (int i = middleIndex + 1; i < max; i++) {
            ActorGameObject card = pickedCards.get(i);
            float t = i - middleIndex;
            float x = mx + (getRealWidth(card) * t) + spacing * t;

            card.actorTransform.actor.addAction(Actions.moveTo(x, my,
                    1f, Interpolation.pow5Out));

            card.transform.setPosition(mx, my);
            addGameObject(card);
        }
    }

    private void cardSelected() {
        canPlay = false;

        Card card = userChoice.getComponent(Card.class);
        float x = userChoice.transform.getX();
        float y = userChoice.transform.getY();

        placeInCenterOf(userChoice, table);
        userChoice.transform.setPosition(userChoice.transform().getX(), worldUnits.toWorldUnit(10));

        Vector2 realSize = card.getRealSize();

        Actor actor = userChoice.actorTransform.actor;
        actor.clearActions();

        Action action1 = Actions.moveTo(userChoice.transform.getX(), userChoice.transform.getY(),
                .7f, Interpolation.pow5Out);
        Action action2 = Actions.scaleTo(1f, 1f, .7f, Interpolation.pow5Out);

        actor.setSize(realSize.x, realSize.y);
        actor.setScale(Card.HIDDEN_SCALE);
        actor.setOrigin(realSize.x / 2f, realSize.y / 2f);
        actor.addAction(Actions.sequence(action1, Actions.run(() -> card.setRevealed(true)), action2));

        userChoice.transform.setPosition(x, y);
        pickedCards.removeValue(userChoice, true);
        playForOpponent();
    }

    private void playForOpponent() {
        opponentChoice = pickedCards.random();
//        System.out.println("Initial choice: " + opponentChoice.getComponent(Card.class).number);
//        System.out.println("Max choice: " + getMaximumPick().getComponent(Card.class).number);
//        System.out.println("Min choice: " + getMinimumPick().getComponent(Card.class).number);

        // Get the user chosen number
        int userNumber = userChoice.getComponent(Card.class).number;

        if (MathUtils.randomBoolean(Constants.WINNING_CHANCE)) {
            //System.out.println("User to win");

            // Make sure the user wins
            if (gameType == GameType.HIGHER)
                opponentChoice = getMinimumPick();
            else
                opponentChoice = getMaximumPick();
        }
        else {
            // Make sure the opponent wins

            // If by chance the cards are equal, let it be
            if (userNumber != opponentChoice.getComponent(Card.class).number) {
                if (gameType == GameType.HIGHER) {
                    // If the user chose the highest card then the user wins else we look for a higher card
                    if (userNumber < getMaximumPick().getComponent(Card.class).number) {
                        while (userNumber > opponentChoice.getComponent(Card.class).number)
                            opponentChoice = pickedCards.random();
                    }
                }
                else {
                    // If the user chose the lowest card then the user wins else we look for a lower card
                    if (userNumber > getMinimumPick().getComponent(Card.class).number) {
                        while (userNumber < opponentChoice.getComponent(Card.class).number)
                            opponentChoice = pickedCards.random();
                    }
                }
            }
        }

        Transform ot = opponent.transform;
        Card card = opponentChoice.getComponent(Card.class);
        card.setOpponentSelected();
        float x = opponentChoice.transform.getX();
        float y = opponentChoice.transform.getY();

        opponentChoice.transform.setPosition(ot.getX() + (ot.getWidth() - card.getRealWidth())/2f,
                ot.getY() - card.getRealHeight());

        Actor actor = opponentChoice.actorTransform.actor;
        actor.clearActions();

        Action action1 = Actions.moveTo(opponentChoice.transform.getX(), opponentChoice.transform.getY(),
                .7f, Interpolation.pow5Out);

        actor.setSize(opponentChoice.transform.getWidth(), opponentChoice.transform.getHeight());
        actor.addAction(Actions.sequence(action1, Actions.run(this::revealChoices)));

        opponentChoice.transform.setPosition(x, y);

        pickedCards.removeValue(opponentChoice, true);
    }

    private void revealChoices() {
        // Reveal and resize the remaining cards
        for (GameObject card : pickedCards)
            card.getComponent(Card.class).setGameOverRevealed();
        placeCards(true);

        // Reveal the opponent's card
        Card card = opponentChoice.getComponent(Card.class);
        card.setRevealed(true);

        Transform ot = opponent.transform;
        opponentChoice.transform.setPosition(ot.getX() + (ot.getWidth() - card.getRealWidth())/2f,
                ot.getY() - card.getRealHeight());

        Actor actor = opponentChoice.actorTransform.actor;
        actor.setPosition(opponentChoice.transform.getX(), opponentChoice.transform.getY());
        actor.setSize(opponentChoice.transform.getWidth(), opponentChoice.transform.getHeight());
    }

    private ActorGameObject getMaximumPick() {
        int max = 0;
        ActorGameObject currentCard = pickedCards.first();
        for (ActorGameObject card : pickedCards) {
            int number = card.getComponent(Card.class).number;
            if (number > max) {
                currentCard = card;
                max = number;
            }
        }

        return currentCard;
    }

    private ActorGameObject getMinimumPick() {
        ActorGameObject currentCard = pickedCards.first();
        int min = currentCard.getComponent(Card.class).number;

        for (ActorGameObject card : pickedCards) {
            int number = card.getComponent(Card.class).number;
            if (number < min) {
                currentCard = card;
                min = number;
            }
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
            if (!canPlay)
                return;

            for (ActorGameObject card : pickedCards) {
                if (card.getHostScene() != null && card.getComponent(Card.class).isTouched(touchEventData.touchX, touchEventData.touchY)) {
                    userChoice = card;
                    cardSelected();
                    break;
                }
            }
        }
    }
}




























