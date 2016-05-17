package com.stickshooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.stickshooter.PixClient;
import com.stickshooter.PixServer;

public class DesktopLauncher {

	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		if (arg.length > 0) {

			if (arg[0].equals("-s")) {

				config.width = 1280;
				config.height = 720;
				config.fullscreen = false;
				config.resizable = false;
				new LwjglApplication(new PixServer(), config);

			}

		} else {

				config.width = 1280;
				config.height = 720;
				config.fullscreen = false;
				config.resizable = true;
				new LwjglApplication(new PixClient(), config);

		}

	}
}
