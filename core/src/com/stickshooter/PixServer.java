package com.stickshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.stickshooter.networking.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Marian on 10.04.2016.
 */
public class PixServer extends Game{
    //podstawowe parametry gry
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

    public SpriteBatch batch;

    @Override
    public void create () {

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        batch = new SpriteBatch();

        setScreen(new Server(this));

    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose() {

        manager.dispose();

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
