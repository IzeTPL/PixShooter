package com.stickshooter.networking;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixShooter;

import java.util.ArrayList;

/**
 * Created by Marian on 10.04.2016.
 */
public class Player{

    public World world;
    public Body body;
    public String login;
    public Server server;
    public int ID;

    private OrthographicCamera orthographicCamera;
    private Viewport viewport;

    public ArrayList<Bullet> bullets;

    public Player(Server server, int ID) {

        this.server = server;
        this.world = server.getWorld();
        this.orthographicCamera = server.getOrthographicCamera();
        this.viewport = server.getViewport();
        this.ID = ID;

        bullets = new ArrayList<Bullet>();

        definePlayer();

    }

    public void definePlayer() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(PixShooter.downScale(32 + 8), PixShooter.downScale(48 + 8));
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(PixShooter.downScale((PixShooter.TILE_SIZE - 2) / 2f));
        fdef.filter.categoryBits = PixShooter.MARIO_BIT;
        fdef.filter.maskBits = PixShooter.DEFAULT_BIT | PixShooter.COIN_BIT | PixShooter.BRICK_BIT;

        fdef.shape = shape;
        body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(PixShooter.downScale(-2),PixShooter.downScale(7) ), new Vector2(PixShooter.downScale(2), PixShooter.downScale(7) ) );
        fdef.shape = head;
        fdef.isSensor = true;

        body.createFixture(fdef).setUserData("head");
    }

    public void move( int movementType )
    {
        switch ( movementType )
        {
            case MovementType.JUMP:
                body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
                break;
            case MovementType.RIGHT:
                body.applyLinearImpulse(new Vector2(0.1f, 0), body.getWorldCenter(), true);
                break;
            case MovementType.LEFT:
                body.applyLinearImpulse(new Vector2(-0.1f, 0), body.getWorldCenter(), true);
                break;

        }
    }

    public void shoot() {



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
