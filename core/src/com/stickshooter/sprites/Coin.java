package com.stickshooter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.stickshooter.PixShooter;
import com.stickshooter.scenes.Hud;

/**
 * Created by Marian on 06.03.2016.
 */
public class Coin extends InteractiveTileObject{

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(World world, TiledMap map, Rectangle bounds) {

        super(world, map, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(PixShooter.COIN_BIT);

    }

    @Override
    public void onHeadHit() {

        Gdx.app.log("Coin", "Collision");
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(400);

    }

    @Override
    public void onBulletHit() {

    }

}
