package com.stickshooter.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.stickshooter.PixShooter;
import com.stickshooter.sprites.Brick;
import com.stickshooter.sprites.Coin;

/**
 * Created by Marian on 06.03.2016.
 */
public class B2WorldCreator {

    public B2WorldCreator(World world, TiledMap map) {

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;


        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(PixShooter.downScale(rect.getX() + rect.getWidth()/2), PixShooter.downScale(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bdef);

            shape.setAsBox(PixShooter.downScale(rect.getWidth()/2), PixShooter.downScale(rect.getHeight()/2));
            fdef.shape = shape;
            body.createFixture(fdef);

        }

        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(PixShooter.downScale(rect.getX() + rect.getWidth()/2), PixShooter.downScale(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bdef);

            shape.setAsBox(PixShooter.downScale(rect.getWidth()/2), PixShooter.downScale(rect.getHeight()/2));
            fdef.shape = shape;
            body.createFixture(fdef);

        }

        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Coin(world, map, rect);

        }

        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(world, map, rect);

        }




    }

}
