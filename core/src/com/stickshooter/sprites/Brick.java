package com.stickshooter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.stickshooter.PixShooter;
import com.stickshooter.scenes.Hud;

/**
 * Created by Marian on 06.03.2016.
 */
public class Brick extends InteractiveTileObject{

    public Brick(World world, TiledMap map, Rectangle bounds) {

        super(world, map, bounds);
        fixture.setUserData(this);
        setCategoryFilter(PixShooter.BRICK_BIT);


    }

    @Override
    public void onHeadHit() {

        Gdx.app.log("Brick", "Collision");
        setCategoryFilter(PixShooter.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);


    }

    @Override
    public void onBulletHit() {

    }

}
