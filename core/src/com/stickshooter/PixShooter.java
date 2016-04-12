package com.stickshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.stickshooter.networking.Server;
import com.stickshooter.screens.PlayScreen;


public class PixShooter extends Game {

    //podstawowe parametry gry
    public SpriteBatch batch;
    public static final int V_WIDTH = 1280;
    public static final int V_HEIGHT = 720;
    public static final float PIXELS_PER_METER = 100;
    public static final float TILE_SIZE = 16;
    public static final float SCALE = 4;


    //flagi bitowe
    public static final short DEFAULT_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short BULLET_BIT = 32;

    //fonty
    public AssetManager manager = new AssetManager();
    public static final String MENU_FONT = "Capture_it.ttf";
    public static final String SIGN_FONT = "GreatVibes-Regular.ttf";

    //Networking
    private Server server;

    @Override
    public void create () {

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));

    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose() {

        batch.dispose();

    }

    public static float upScale(float toScale) {

        return toScale*PIXELS_PER_METER;

    }

    public static float downScale(float toScale) {

        return toScale/PIXELS_PER_METER;

    }

    public static float setRange(float currentValue, float maxValue) {

        return currentValue/maxValue;

    }

    public static float setRange(float currentValue, float maxValue, int range) {

        return range*setRange(currentValue, range);

    }

}

