package com.stickshooter.sprites;

import com.badlogic.gdx.math.Vector2;
import com.stickshooter.prototypes.AbstractBullet;

/**
 * Created by Marian on 20.03.2016.
 */
public class Bullet extends AbstractBullet{

    public Bullet(Player player, float degrees) {

        super(player);
        body.setLinearVelocity(new Vector2(2f, 2f).setAngle(degrees));

    }

}
