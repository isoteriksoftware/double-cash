package com.isoterik.cash4life.double_cash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.isoterik.cash4life.double_cash.Constants;
import com.isoterik.cash4life.double_cash.DoubleCash;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Constants.GUI_WIDTH;
		config.height = Constants.GUI_HEIGHT;
		//config.resizable = false;

		new LwjglApplication(new DoubleCash(), config);
	}
}
