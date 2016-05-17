package com.stickshooter.networking;

import com.badlogic.gdx.math.Vector2;
import com.stickshooter.prototypes.AbstractBullet;

/**
 * Created by Marian on 20.03.2016.
 */
public class Bullet extends AbstractBullet {

    public Bullet(Player player, float degrees) {

        super(player, degrees);
        body.setLinearVelocity(new Vector2(1f, 1f).setAngle(degrees));

    }

}
