package com.stickshooter.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

/**
 * Created by Marian on 17.03.2016.
 */
public class FTFontGenerator{

    private FreetypeFontLoader.FreeTypeFontLoaderParameter parameter;
    private BitmapFont font;
    private AssetManager manager;

    public FTFontGenerator(AssetManager manager) {

        this.manager = manager;
        parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();

    }

    public BitmapFont generateFont (String name, int size) {

        parameter.fontFileName = name;
        parameter.fontParameters.size = size;
        manager.load(size + "Capture_it.ttf", BitmapFont.class, parameter);
        manager.finishLoading();
        font = manager.get(size + "Capture_it.ttf", BitmapFont.class);
        return font;
    }

    public void dispose() {

    }

}
