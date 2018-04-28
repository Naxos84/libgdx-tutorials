package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
    public static void main(final String[] arg) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "My Gdx Game";
        config.width = 800;
        config.height = 600;
        new LwjglApplication(new MyGdxGame(), config);
    }
}
