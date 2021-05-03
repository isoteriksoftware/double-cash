package com.isoterik.cash4life.double_cash.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.scenes.GamePlayScene;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.io.GameAssetsLoader;
import com.isoterik.mgdx.ui.ActorAnimation;

public final class UIHelper {
    private Stage canvas;
    private GameAssetsLoader assetsLoader;
    private Skin skin;

    public Label balanceLabel;
    public Button btnDeposit, btnSettings, btnHelp, btnSound, btnQuit;
    public Window menuWindow, menuSelectorWindow;
    private Table menuContainer;

    public UIHelper(Stage canvas) {
        this.canvas = canvas;
        this.assetsLoader = MinGdx.instance().assets;
        this.skin = assetsLoader.getSkin(Constants.SKIN);
    }

    public void showYourTurn() {
        Label label = new Label("YOUR  TURN", skin, "white64");
        label.setAlignment(Align.center);

        Window window = newTypedWindow("info");
        window.pad(20);
        window.add(label).padRight(20).padLeft(20);

        window.pack();
        centerActorOrigin(window);
        centerActor(window, canvas);
        canvas.addActor(window);

        ActorAnimation.instance().grow(window, .7f, Interpolation.bounceOut);
        window.addAction(Actions.delay(1.5f, Actions.run(() -> {
            ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, .7f, Interpolation.swingOut);
        })));
    }

    public void showOpponentTurn(Runnable onReady) {
        Label label = new Label("OPPONENT  TURN", skin, "white64");
        label.setAlignment(Align.center);

        Window window = newTypedWindow("info");
        window.pad(20);
        window.add(label).padRight(20).padLeft(20);

        window.pack();
        centerActorOrigin(window);
        centerActor(window, canvas);
        canvas.addActor(window);

        ActorAnimation.instance().grow(window, .7f, Interpolation.bounceOut);
        window.addAction(Actions.delay(1.5f, Actions.run(() -> {
            ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, .7f, Interpolation.swingOut);
            onReady.run();
        })));
    }

    public Window newWindow() {
        Window window = new Window("", skin);
        window.setKeepWithinStage(false);
        window.setModal(true);
        int pad = 40;
        window.padLeft(pad).padRight(pad).padBottom(pad);

        return window;
    }

    public Window newTypedWindow(String type) {
        Window window = new Window("", skin, type);
        window.setKeepWithinStage(false);
        window.setModal(true);
        int pad = 40;
        window.padLeft(pad).padRight(pad).padBottom(pad);

        return window;
    }

    public void showStakeDialog(StakeListener stakeListener) {
        Window window = newWindow();

        Label title = new Label("What's your stake?".toUpperCase(), skin, "green");
        title.setAlignment(Align.center);

        Label amount = new Label("Amount: ", skin, "green24");
        TextField stake = new TextField("100", skin);
        stake.clearListeners();
        stake.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        try {
                            Integer.parseInt(text);
                            stake.setText(text);
                        } catch (Exception ignored) {}
                    }

                    @Override
                    public void canceled() {

                    }
                }, "Enter Stake Amount", stake.getText(), "", Input.OnscreenKeyboardType.NumberPad);
            }
        });

        TextButton btnHighest = new TextButton("Highest Card", skin);
        btnHighest.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float duration = .5f;
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out);
                window.addAction(Actions.delay(duration, Actions.run(() -> stakeListener.onStake(GamePlayScene.GameType.HIGHER, Integer.parseInt(stake.getText())))));
            }
        });

        TextButton btnLowest = new TextButton("Lowest Card", skin);
        btnLowest.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float duration = .5f;
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out);
                window.addAction(Actions.delay(duration, Actions.run(() -> stakeListener.onStake(GamePlayScene.GameType.LOWER, Integer.parseInt(stake.getText())))));
            }
        });

        Table stakeTbl = new Table();
        stakeTbl.left();
        stakeTbl.add(amount).padRight(20);
        stakeTbl.add(stake).height(40).expandX().fillX();

        window.top().padTop(50);
        window.add(title).expandX().fillX().left().colspan(2);
        window.row().padTop(50);
        window.add(stakeTbl).expandX().fillX().colspan(2);
        window.row().padTop(30);
        window.add(btnHighest).expandX().fillX().padRight(30);
        window.add(btnLowest).expandX().fillX();

        window.pack();

        canvas.addActor(window);
        centerActor(window, canvas);
        centerActorOrigin(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }

    public void showGameOverDialog(String status, String earning, GameOverListener gameOverListener) {
        Window window = newWindow();

        Label statusLbl = new Label(status, skin, "green64");
        statusLbl.setAlignment(Align.center);

        Label earningsLabel = new Label("EARNINGS", skin, "main32");
        Label earnings = new Label(earning, skin, "money");
        Table labelTbl = new Table();
        labelTbl.left();
        labelTbl.add(earningsLabel).padRight(30);
        labelTbl.add(earnings).expandX().fillX();

        Button btnHome = new Button(skin, "home");
        btnHome.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float duration = .5f;
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out);
                window.addAction(Actions.delay(duration, Actions.run(() -> gameOverListener.onAction(GameOverListener.Action.HOME))));
            }
        });

        Button btnRestart = new Button(skin, "restart");
        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float duration = .5f;
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out);
                window.addAction(Actions.delay(duration, Actions.run(() -> gameOverListener.onAction(GameOverListener.Action.RESTART))));
            }
        });

        Button btnQuit = new Button(skin, "quit");
        btnQuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float duration = .5f;
                ActorAnimation.instance().slideOutThenRemove(window, ActorAnimation.UP, duration, Interpolation.pow5Out);
                window.addAction(Actions.delay(duration, Actions.run(() -> gameOverListener.onAction(GameOverListener.Action.QUIT))));
            }
        });

        Table bottom = new Table();
        int pad = 20;
        bottom.add(btnHome).expandX().padRight(pad);
        bottom.add(btnRestart).expandX().padRight(pad);
        bottom.add(btnQuit).expandX();

        window.top().padTop(50);
        window.add(statusLbl).expandX().fillX().left().colspan(2);
        window.row().padTop(100);
        window.add(labelTbl).expandX().fillX();
        window.row().padTop(50);
        window.add(bottom).bottom().expand().fillX();

        canvas.addActor(window);
        window.pack();
        centerActor(window, canvas);
        centerActorOrigin(window);
        ActorAnimation.instance().slideIn(window, ActorAnimation.DOWN, .7f, Interpolation.swingOut);
    }

    public void setupUI(MenuListener menuListener) {
        Window window = newTypedWindow("balance");
        window.setModal(false);
        window.pad(0).padLeft(20);

        menuSelectorWindow = newTypedWindow("balance");
        menuSelectorWindow.setModal(false);
        menuSelectorWindow.pad(0);

        menuWindow = newTypedWindow("balance-modal");
        menuWindow.pad(10);

        balanceLabel = new Label("000000", skin, "main24");
        //balanceLabel.setAlignment(Align.center);
        balanceLabel.pack();

        Label settings = new Label("Menu", skin, "main32");
        settings.pack();

        btnDeposit = new Button(skin, "plus-small");
        btnSettings = new Button(skin, "settings-small");
        btnQuit = new Button(skin, "quit-small");
        btnSound = new Button(skin, "sound-small");
        btnHelp = new Button(skin, "help-small");

        btnSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMenu();
            }
        });

        btnQuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuContainer.addAction(Actions.delay(.5f, Actions.run(
                        () -> menuListener.onAction(MenuListener.Action.SOUND)
                )));
            }
        });

        btnHelp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuContainer.addAction(Actions.delay(.5f, Actions.run(
                        () -> menuListener.onAction(MenuListener.Action.SOUND)
                )));
            }
        });

        btnSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuContainer.addAction(Actions.delay(.5f, Actions.run(
                        () -> menuListener.onAction(MenuListener.Action.SOUND)
                )));
            }
        });

        window.add(balanceLabel).left().expandX();
        window.add(btnDeposit).right();

        menuSelectorWindow.add(btnSettings).left();
        menuSelectorWindow.add(settings).right().expandX().padRight(20);

        menuWindow.add(btnHelp).left();
        menuWindow.add(btnSound).expandX();
        menuWindow.add(btnQuit).right();
        menuWindow.setKeepWithinStage(false);

        menuSelectorWindow.pack();
        menuSelectorWindow.setX(canvas.getWidth() - menuSelectorWindow.getWidth() - 20);
        menuSelectorWindow.setY(canvas.getHeight() - menuSelectorWindow.getHeight() - 20);

        window.pack();
        window.setX(20);
        window.setY(canvas.getHeight() - window.getHeight() - 20);

        menuWindow.pack();
        menuWindow.setX(menuSelectorWindow.getX());
        menuWindow.setY(menuSelectorWindow.getY() - menuWindow.getHeight());

        canvas.addActor(window);
        canvas.addActor(menuSelectorWindow);
    }

    private void closeMenu() {
        float duration = .5f;
        ActorAnimation.instance().slideOutThenRemove(menuContainer, ActorAnimation.RIGHT, duration,
                Interpolation.pow5In);
    }

    private void showMenu() {
        float duration = .5f;

        menuContainer = new Table();
        menuContainer.setFillParent(true);
        menuContainer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (menuContainer.hasParent()) {
                    closeMenu();
                }
            }
        });

        menuContainer.top().right();
        menuContainer.add(menuWindow).pad(20).padTop(menuSelectorWindow.getHeight() + 20);

        ActorAnimation.instance().slideIn(menuContainer, ActorAnimation.RIGHT, duration,
                Interpolation.pow5Out);
        canvas.addActor(menuContainer);
    }

    public static void centerActor(Actor actor, Stage canvas) {
        actor.setX((canvas.getWidth() - actor.getWidth())/2f);
        actor.setY((canvas.getHeight() - actor.getHeight())/2f);
    }

    public static void centerActorOrigin(Actor actor) {
        actor.setOriginX(actor.getWidth()/2f);
        actor.setOriginY(actor.getHeight()/2f);
    }

    public interface StakeListener {
        void onStake(GamePlayScene.GameType gameType, int amount);
    }

    public interface GameOverListener {
        enum Action {
            HOME, RESTART, QUIT
        }

        void onAction(Action action);
    }

    public interface MenuListener {
        enum Action {
            HELP, SOUND, QUIT
        }

        void onAction(Action action);
    }
}

























