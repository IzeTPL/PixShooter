package com.stickshooter.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Marian on 25.04.2016.
 */
public class DrawableColor {

    public static TextureRegionDrawable getColor(Color color) {

        return getColor(color, 1);

    }

    public static TextureRegionDrawable getColor(Color color, float alpha) {

        return getColor(color.r, color.g, color.b, alpha);

    }

    public static TextureRegionDrawable getColor(float r, float g, float b, float a) {

        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();

        return new TextureRegionDrawable(new TextureRegion(new Texture(pixmap) ) );

    }

}
