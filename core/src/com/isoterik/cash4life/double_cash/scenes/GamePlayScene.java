package com.isoterik.cash4life.double_cash.scenes;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.components.Card;
import com.isoterik.cash4life.double_cash.utils.PreferenceHelper;
import com.isoterik.cash4life.double_cash.utils.UIHelper;
import com.isoterik.mgdx.*;
import com.isoterik.mgdx.audio.AudioManager;
import com.isoterik.mgdx.input.ITouchListener;
import com.isoterik.mgdx.input.TouchEventData;
import com.isoterik.mgdx.input.TouchTrigger;
import com.isoterik.mgdx.m2d.components.debug.BoxDebugRenderer;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;
import com.isoterik.mgdx.m2d.scenes.transition.TransitionDirection;
import com.isoterik.mgdx.ui.ActorAnimation;
import com.isoterik.mgdx.utils.WorldUnits;

public class GamePlayScene extends Scene {
    private MinGdx minGdx;
    private WorldUnits worldUnits;

    private Array<ActorGameObject> cards, pickedCards;
    private final TextureRegion cardBackSprite;

    private final GameObject table, opponent;
    private ActorGameObject userChoice, opponentChoice;

    public enum GameType { HIGHER, LOWER }
    private enum Turn { USER, OPPONENT }

    private GameType gameType = GameType.HIGHER;
    private Turn turn;
    private int stakeAmount = 0;
    private int balance = 5000;
    private int played = 0;
    private boolean canPlay = false;
    private boolean opponentHasPlayed = false;

    private UIHelper uiHelper;
    private UIHelper.StakeListener stakeListener;
    private UIHelper.GameOverListener gameOverListener;
    private Runnable onOpponentReady;

    private UIHelper.MenuListener menuListener;

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

        canvas = new Stage(new StretchViewport(Constants.GUI_WIDTH, Constants.GUI_HEIGHT));
        inputManager.getInputMultiplexer().addProcessor(canvas);

        ActorAnimation.instance().setup(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        uiHelper = new UIHelper(canvas);
        menuListener = action -> {
            switch (action) {
                case QUIT:
                    quit();
                    break;
                case HELP:
                    Gdx.net.openURI("https://cash4life.com.ng/double-cash/help");
                    break;
                case SOUND:
                    PreferenceHelper.instance().setSoundEnabled(!uiHelper.btnSound.isChecked());
                    PreferenceHelper.instance().saveChanges();
                    break;
            }
        };

        uiHelper.setupUI(menuListener);
        uiHelper.balanceLabel.setText(balance);

        onOpponentReady = () -> {
            int waitPeriod = MathUtils.random(1, 3);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (userChoice == null) {
                        // If we're playing first, we randomly guess what the user will choose
                        playForOpponent(pickedCards.random());
                    }
                    else
                        playForOpponent();
                }
            }, waitPeriod);
        };

        stakeListener = (gameType, amount) -> {
            this.gameType = gameType;
            this.stakeAmount = amount;
            balance -= amount;
            uiHelper.balanceLabel.setText(balance);

            placeCards(false);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (MathUtils.randomBoolean(Constants.STARTING_CHANCE))
                        startUserTurn();
                    else
                        startOpponentTurn();
                }
            }, 1f);
        };

        gameOverListener = action -> {
            switch (action) {
                case RESTART:
                    newGame();
                    break;
                case HOME:
                    goHome();
                    break;
                case QUIT:
                    quit();
            }
        };
    }

    private void goHome() {
        minGdx.sceneManager.revertToPreviousScene(SceneTransitions.slide(1f, TransitionDirection.DOWN,
                false, Interpolation.pow5Out));
    }

    private void quit() {
        goHome();
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
        if (played == 2) {
            pickedCards.add(userChoice, opponentChoice);

            for (ActorGameObject gameObject : pickedCards) {
                gameObject.getComponent(Card.class).setRevealed(false);
                removeGameObject(gameObject);
            }
        }

        pickRandomCards();
        centerCards();
        for (ActorGameObject gameObject : pickedCards)
            addGameObject(gameObject);

        userChoice = null;
        opponentChoice = null;
        opponentHasPlayed = false;
        played = 0;
        uiHelper.showStakeDialog(stakeListener, balance);
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

        float spacing = worldUnits.toWorldUnit(10);

        for (int i = 0; i < middleIndex; i++) {
            ActorGameObject card = pickedCards.get(i);
            float t = middleIndex - i;
            float x = mx - (getRealWidth(card) * t) - spacing * t;

            card.actorTransform.actor.addAction(Actions.moveTo(x, my,
                    1f, Interpolation.pow5Out));

            card.transform.setPosition(mx, my);
        }

        for (int i = middleIndex + 1; i < max; i++) {
            ActorGameObject card = pickedCards.get(i);
            float t = i - middleIndex;
            float x = mx + (getRealWidth(card) * t) + spacing * t;

            card.actorTransform.actor.addAction(Actions.moveTo(x, my,
                    1f, Interpolation.pow5Out));

            card.transform.setPosition(mx, my);
        }

        // Play the sound
        AudioManager.instance().playSound(minGdx.assets.getSound("sfx/cardPlace.ogg"), 1f);
    }

    private void centerCards() {
        int max = pickedCards.size;

        // Calculate the position of the middle card
        int middleIndex = max/2;
        ActorGameObject middle = pickedCards.get(middleIndex);
        float mx = (table.transform.getX() + table.transform.getWidth()/2f) - middle.transform.getWidth()/2f;
        float my = (table.transform.getY() + table.transform.getHeight()/2f) - middle.transform.getHeight()/2f;

        for (ActorGameObject gameObject : pickedCards)
            gameObject.actorTransform.setPosition(mx, my);
    }

    private void userPlayed() {
        canPlay = false;
        played++;

        Transform ot = opponent.transform;
        Card card = userChoice.getComponent(Card.class);
        float x = userChoice.transform.getX();
        float y = userChoice.transform.getY();

        userChoice.transform.setPosition(ot.getX() + (ot.getWidth() - card.getRealWidth())/2f,
                ot.getY() - card.getRealHeight());
        userChoice.transform.setPosition(userChoice.transform().getX() - worldUnits.toWorldUnit(15),
                worldUnits.toWorldUnit(10));

        Vector2 realSize = card.getRealSize();

        Actor actor = userChoice.actorTransform.actor;
        actor.clearActions();

        Action action1 = Actions.moveTo(userChoice.transform.getX(), userChoice.transform.getY(),
                .7f, Interpolation.pow5Out);
        Action action2 = Actions.scaleTo(1f, 1f, .7f, Interpolation.pow5Out);

        actor.setSize(realSize.x, realSize.y);
        actor.setScale(Card.HIDDEN_SCALE);
        actor.setOrigin(realSize.x / 2f, realSize.y / 2f);
        actor.addAction(Actions.sequence(action1, Actions.run(() -> card.setRevealed(true)), action2,
                Actions.run(() -> {
                    // Play for opponent if not played. Else end the game
                    if (played == 1)
                        startOpponentTurn();
                    else
                        finishGame();
                })));

        userChoice.transform.setPosition(x, y);
        pickedCards.removeValue(userChoice, true);

        playSlideSound();
    }

    private void playForOpponent(ActorGameObject userChoice) {
        opponentChoice = pickedCards.random();
        played++;

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
        actor.addAction(Actions.sequence(action1, Actions.run(() -> {
            if (played == 1)
                startUserTurn();
            else
                finishGame();
        })));

        opponentChoice.transform.setPosition(x, y);
        pickedCards.removeValue(opponentChoice, true);

        playSlideSound();
    }

    private void playForOpponent() {
        playForOpponent(userChoice);
    }

    private void startUserTurn() {
        canPlay = true;
        turn = Turn.USER;
        uiHelper.showYourTurn();
    }

    private void startOpponentTurn() {
        canPlay = false;
        turn = Turn.OPPONENT;
        uiHelper.showOpponentTurn(onOpponentReady);
    }

    private void finishGame() {
        revealChoices();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                int userNumber = userChoice.getComponent(Card.class).number;
                int opponentNumber = opponentChoice.getComponent(Card.class).number;

                if (userNumber == opponentNumber) {
                    int won = stakeAmount / 2;
                    balance += won;
                    uiHelper.showGameOverDialog("IT's A TIE", "+" + won, gameOverListener);
                    playTieSound();
                }
                else {
                    int stake = stakeAmount;
                    int won = 0;
                    if (gameType == GameType.HIGHER) {
                        if (userNumber > opponentNumber) {
                            won = stake * 2;
                            uiHelper.showGameOverDialog("YOU WIN", "+" + won, gameOverListener);
                            playWinSound();
                        }
                        else {
                            uiHelper.showGameOverDialog("YOU LOSE", "-" + stake, gameOverListener);
                            playLoseSound();
                        }
                    }
                    else {
                        if (userNumber < opponentNumber) {
                            won = stake * 2;
                            uiHelper.showGameOverDialog("YOU WIN", "+" + won, gameOverListener);
                            playWinSound();
                        }
                        else {
                            uiHelper.showGameOverDialog("YOU LOSE", "-" + stake, gameOverListener);
                            playLoseSound();
                        }
                    }

                    balance += won;
                }

                uiHelper.balanceLabel.setText(balance);
            }
        }, 2f);
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

    private void playSlideSound() {
        // Play the sound
        AudioManager.instance().playSound(minGdx.assets.getSound("sfx/cardSlide.ogg"), 1f);
    }

    private void playWinSound() {
        // Play the sound
        AudioManager.instance().playSound(minGdx.assets.getSound("sfx/you_win.ogg"), 1f);
    }

    private void playLoseSound() {
        // Play the sound
        AudioManager.instance().playSound(minGdx.assets.getSound("sfx/you_lose.ogg"), 1f);
    }

    private void playTieSound() {
        // Play the sound
        AudioManager.instance().playSound(minGdx.assets.getSound("sfx/its_a_tie.ogg"), 1f);
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

    public class CardClickListener implements ITouchListener {
        @Override
        public void onTouch(String mappingName, TouchEventData touchEventData) {
            if (!canPlay || turn != Turn.USER)
                return;

            for (ActorGameObject card : pickedCards) {
                if (card.getHostScene() != null && card.getComponent(Card.class).isTouched(touchEventData.touchX, touchEventData.touchY)) {
                    userChoice = card;
                    userPlayed();
                    break;
                }
            }
        }
    }
}




























