package com.stickshooter.prototypes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixClient;
import com.stickshooter.sprites.Bullet;

import java.util.ArrayList;

/**
 * Created by Marian on 16.05.2016.
 */
public abstract class AbstractPlayer {

    public enum State { FALLING, JUMPING, STANDING, RUNNING, KICKED, DEAD }
    public State currentState;
    public State previousState;

    public World world;
    public Body body;

    protected OrthographicCamera orthographicCamera;
    protected Viewport viewport;

    public AbstractPlayer(AbstractPlayScreen screen) {

        this.world = screen.getWorld();
        this.orthographicCamera = screen.getGamecam();
        this.viewport = screen.getGameViewport();

        definePlayer();

    }

    public void definePlayer() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(PixClient.downScale(32 + 8), PixClient.downScale(48 + 8));
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(PixClient.downScale((PixClient.TILE_SIZE - 2) / 2f));
        fdef.filter.categoryBits = PixClient.PLAYER_BIT;
        fdef.filter.maskBits = PixClient.DEFAULT_BIT | PixClient.COIN_BIT | PixClient.BRICK_BIT | PixClient.OBJECT_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(PixClient.downScale(-2), PixClient.downScale(7) ), new Vector2(PixClient.downScale(2), PixClient.downScale(7) ) );
        fdef.filter.categoryBits = PixClient.HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        body.createFixture(fdef).setUserData(this);
    }

    public World getWorld() {
        return world;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getOrthographicCamera() {
        return orthographicCamera;
    }

}
