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
import com.stickshooter.screens.MainMenuScreen;


public class StickShooter extends Game {

    //podstawowe parametry gry
    public SpriteBatch batch;
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;


    //flagi bitowe
    public static final short DEFAULT_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;

    //fonty
    public AssetManager manager = new AssetManager();
    public static final String MENU_FONT = "Capture_it.ttf";
    public static final String SIGN_FONT = "GreatVibes-Regular.ttf";




    @Override
    public void create () {

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));

    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose() {

        batch.dispose();

    }
}

