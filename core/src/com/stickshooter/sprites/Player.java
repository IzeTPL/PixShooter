package com.stickshooter.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.stickshooter.PixClient;
import com.stickshooter.prototypes.AbstractPlayer;
import com.stickshooter.screens.PlayScreen;

import java.util.ArrayList;

/**
 * Created by Marian on 06.03.2016.
 */
public class Player extends AbstractPlayer{

    public ArrayList<Bullet> bullets;

    private Sprite stand;
    private Animation run;
    private Animation jump;
    private float stateTimer;
    private boolean runningRight;
    public Sprite sprite;

    public Player(PlayScreen screen) {

        super(screen);
        super.definePlayer();

        bullets = new ArrayList<>();

        sprite = new Sprite(screen.getAtlas().findRegion("little_mario"));

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for(int i = 1; i < 4; i++) {

            frames.add(new TextureRegion(sprite.getTexture(), i * 16, 11, 16, 16));

        }

        run = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 4; i < 6; i++) {

            frames.add(new TextureRegion(sprite.getTexture(), i * 16, 11, 16, 16));

        }

        jump = new Animation(0.1f, frames);
        stand = new Sprite(sprite.getTexture(), 1, 11, 16, 16);
        sprite.setBounds(0, 0, PixClient.downScale(16), PixClient.downScale(16));
        sprite.setRegion(stand);

    }

    @Override
    public void update(float dt){

        sprite.setPosition(body.getPosition().x - sprite.getWidth()/2, body.getPosition().y - sprite.getHeight()/2);
        sprite.setRegion(getFrame(dt));

        if(currentState == State.DEAD) {

            remove = true;

        }

        if(respawn) {

            respawn = false;
            lifeTimer = 0;
            definePlayer();

        }

    }

    public TextureRegion getFrame(float dt) {

        currentState = getState();

        TextureRegion region;
        switch(currentState) {

            case JUMPING:
                region = jump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = run.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = stand;
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

}
