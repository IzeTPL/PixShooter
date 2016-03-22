package com.stickshooter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.stickshooter.StickShooter;
import com.stickshooter.scenes.Hud;

/**
 * Created by Marian on 06.03.2016.
 */
public class Brick extends InteractiveTileObject{

    public Brick(World world, TiledMap map, Rectangle bounds) {

        super(world, map, bounds);
        fixture.setUserData(this);
        setCategoryFilter(StickShooter.BRICK_BIT);


    }

    @Override
    public void onHeadHit() {

        Gdx.app.log("Brick", "Collision");
        setCategoryFilter(StickShooter.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);


    }

}
