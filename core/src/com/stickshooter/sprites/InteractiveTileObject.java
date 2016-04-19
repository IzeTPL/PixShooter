package com.stickshooter.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.stickshooter.PixShooter;

/**
 * Created by Marian on 06.03.2016.
 */
public abstract class InteractiveTileObject {

    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;


    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds) {

        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;

        bdef.position.set(PixShooter.downScale(bounds.getX() + bounds.getWidth()/2), PixShooter.downScale(bounds.getY() + bounds.getHeight()/2));

        body = world.createBody(bdef);


        shape.setAsBox(PixShooter.downScale(bounds.getWidth()/2), PixShooter.downScale(bounds.getHeight()/2));

        fdef.shape = shape;

        fixture = body.createFixture(fdef);

    }

    public abstract void onHeadHit();
    public abstract void onBulletHit();
    public void setCategoryFilter(short filterBit) {

        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);

    }

    public TiledMapTileLayer.Cell getCell() {

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * PixShooter.PIXELS_PER_METER/PixShooter.TILE_SIZE), (int)(body.getPosition().y * PixShooter.PIXELS_PER_METER/PixShooter.TILE_SIZE));

    }

}
