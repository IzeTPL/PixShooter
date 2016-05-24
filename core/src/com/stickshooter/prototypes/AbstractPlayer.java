package com.stickshooter.prototypes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.AbstractGame;

/**
 * Created by Marian on 16.05.2016.
 */
public abstract class AbstractPlayer{

    public enum State { FALLING, JUMPING, STANDING, RUNNING, KICKED, DEAD }
    public State currentState;
    public State previousState;

    protected boolean respawn = false;
    protected boolean remove = false;
    protected float lifeTime = 1;
    protected float lifeTimer = 0;

    public World world;
    public Body body;
    protected int score = 0;

    protected OrthographicCamera orthographicCamera;
    protected Viewport viewport;

    public AbstractPlayer(AbstractPlayScreen screen) {

        this.world = screen.getWorld();
        this.orthographicCamera = screen.getOrthographicCamera();
        this.viewport = screen.getGameViewport();

    }

    public void definePlayer() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(AbstractGame.downScale(32 + 8), AbstractGame.downScale(48 + 8));
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(AbstractGame.downScale((AbstractGame.TILE_SIZE - 2) / 2f));
        fdef.filter.categoryBits = AbstractGame.PLAYER_BIT;
        fdef.filter.maskBits = AbstractGame.DEFAULT_BIT | AbstractGame.COIN_BIT | AbstractGame.BRICK_BIT | AbstractGame.OBJECT_BIT | AbstractGame.BULLET_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(AbstractGame.downScale(-2), AbstractGame.downScale(7) ), new Vector2(AbstractGame.downScale(2), AbstractGame.downScale(7) ) );
        fdef.filter.categoryBits = AbstractGame.HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        body.createFixture(fdef).setUserData(this);
    }

    public abstract void update(float dt);

    public World getWorld() {
        return world;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getOrthographicCamera() {
        return orthographicCamera;
    }

    public void setDead() {

        currentState = State.DEAD;

    }

    public void setToRespawn() {

        respawn = true;
        currentState = State.STANDING;
        previousState = State.DEAD;

    }

    public boolean shouldRemove() {

        return remove;

    }

    public boolean shouldRespawn() {

        return respawn;

    }

    public void reset() {

        remove = false;

    }

    public void addScore(int score) {this.score += score;}

}
