package com.stickshooter.networking;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.stickshooter.AbstractGame;
import com.stickshooter.prototypes.AbstractPlayer;

import java.util.ArrayList;

/**
 * Created by Marian on 10.04.2016.
 */
public class Player extends AbstractPlayer{

    public ArrayList<Bullet> bullets;

    public Player(Server server) {

        super(server);
        bullets = new ArrayList<>();
        definePlayer();

    }

    public void move( int movementType ) {

        if(currentState != State.DEAD) {

            switch (movementType) {

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

    @Override
    public void definePlayer() {

        super.definePlayer();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(AbstractGame.downScale((AbstractGame.TILE_SIZE - 2) / 2f));
        fdef.filter.categoryBits = AbstractGame.CLIENT_BIT;
        fdef.filter.maskBits = AbstractGame.DEFAULT_BIT | AbstractGame.COIN_BIT | AbstractGame.BRICK_BIT | AbstractGame.OBJECT_BIT | AbstractGame.BULLET_BIT;
        fdef.isSensor = true;
        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void update(float dt) {

        if(currentState == State.DEAD) {

            remove = true;
            setToRespawn();

        }

        if(respawn) {

            lifeTimer += dt;
            if(lifeTimer > lifeTime) {
                respawn = false;
                lifeTimer = 0;
                definePlayer();
            }

        }

    }

}
