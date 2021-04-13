package com.isoterik.cash4life.double_cash;

import com.badlogic.gdx.graphics.Texture;
import com.isoterik.cash4life.double_cash.scenes.GamePlayScene;
import com.isoterik.mgdx.MinGdxGame;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;

public class DoubleCash extends MinGdxGame {

	@Override
	protected Scene initGame() {
		loadAssets();

		splashTransition = SceneTransitions.fade(1f);
		return new GamePlayScene();
	}

	private void loadAssets() {
		minGdx.assets.enqueueFolderContents("images", Texture.class);
		minGdx.assets.loadAssetsNow();
	}
}
