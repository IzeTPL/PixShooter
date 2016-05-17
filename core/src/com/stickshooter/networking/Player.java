package com.stickshooter.networking;

import com.badlogic.gdx.math.Vector2;
import com.stickshooter.prototypes.AbstractPlayer;

import java.util.ArrayList;

/**
 * Created by Marian on 10.04.2016.
 */
public class Player extends AbstractPlayer{

    public ArrayList<Bullet> bullets;
    public String login;
    public int ID;

    public Player(Server server, int ID) {

        super(server);
        this.ID = ID;
        bullets = new ArrayList<>();

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
