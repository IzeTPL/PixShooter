package com.stickshooter.networking;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixShooter;
import com.stickshooter.screens.PlayScreen;
import com.stickshooter.sprites.Bullet;

import java.util.ArrayList;

/**
 * Created by Marian on 10.04.2016.
 */
public class Player{

    public enum State { FALLING, JUMPING, STANDING, RUNNING, KICKED, DEAD }
    public State currentState;
    public State previousState;

    public World world;
    public Body body;
    public String login;

    private float stateTimer;
    private boolean runningRight;

    private OrthographicCamera orthographicCamera;
    private Viewport viewport;

    public ArrayList<Bullet> bullets;

    public Player(World world, Server screen, OrthographicCamera orthographicCamera , Viewport viewport) {

        this.world = world;
        this.orthographicCamera = orthographicCamera;
        this.viewport = viewport;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

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

    public void shoot() {

        //bullets.add(new Bullet(world, this, orthographicCamera, viewport));

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

}
