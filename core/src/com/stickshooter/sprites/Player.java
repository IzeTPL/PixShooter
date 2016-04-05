package com.stickshooter.sprites;

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

import java.util.ArrayList;

/**
 * Created by Marian on 06.03.2016.
 */
public class Player extends Sprite{

    public enum State { FALLING, JUMPING, STANDING, RUNNING, KICKED, DEAD }
    public State currentState;
    public State previousState;

    public World world;
    public Body body;
    private Sprite stickmanStand;
    private Animation stickmanRun;
    private Animation stickmanJump;
    private float stateTimer;
    private boolean runningRight;

    private OrthographicCamera orthographicCamera;
    private Viewport viewport;

    public ArrayList<Bullet> bullets;

    public Player(World world, PlayScreen screen, OrthographicCamera orthographicCamera , Viewport viewport) {

        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;
        this.orthographicCamera = orthographicCamera;
        this.viewport = viewport;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        bullets = new ArrayList<Bullet>();

        for(int i = 1; i < 4; i++) {

            frames.add(new TextureRegion(getTexture(), i * 16, 11, 16, 16));

        }

        stickmanRun = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 4; i < 6; i++) {

            frames.add(new TextureRegion(getTexture(), i * 16, 11, 16, 16));

        }

        stickmanJump = new Animation(0.1f, frames);


        definePlayer();
        stickmanStand = new Sprite(getTexture(), 1, 11, 16, 16);
        setBounds(0, 0, PixShooter.downScale(16), PixShooter.downScale(16));
        setRegion(stickmanStand);

    }

    public void update(float dt){

        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        setRegion(getFrame(dt));

    }

    public TextureRegion getFrame(float dt) {

        currentState = getState();

        TextureRegion region;
        switch(currentState) {

            case JUMPING:
                region = stickmanJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = stickmanRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = stickmanStand;
                break;

        }

        if((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {

            region.flip(true, false);
            runningRight = false;

        } else if ((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {

            region.flip(true, false);
            runningRight = true;

        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;

    }

    public State getState() {

        if(body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;

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

        bullets.add(new Bullet(world, this, orthographicCamera, viewport));

    }

}
