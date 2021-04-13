package com.isoterik.cash4life.double_cash;

import com.isoterik.mgdx.MinGdxGame;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;

public class DoubleCash extends MinGdxGame {

	@Override
	protected Scene initGame() {
		splashTransition = SceneTransitions.fade(1f);
		return new Scene();
	}
}
