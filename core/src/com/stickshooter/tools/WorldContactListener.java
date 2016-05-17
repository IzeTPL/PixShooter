package com.stickshooter.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.stickshooter.PixClient;
import com.stickshooter.prototypes.AbstractBullet;
import com.stickshooter.prototypes.AbstractTileObject;
import com.stickshooter.sprites.Bullet;

/**
 * Created by Marian on 10.03.2016.
 */
public class WorldContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case PixClient.BULLET_BIT | PixClient.BRICK_BIT:
                if(fixA.getFilterData().categoryBits == PixClient.BULLET_BIT) {
                    ((AbstractTileObject) fixB.getUserData()).onBulletHit();
                    ((AbstractBullet) fixA.getUserData()).remove();
                } else {
                    ((AbstractTileObject) fixA.getUserData()).onBulletHit();
                    ((AbstractBullet) fixB.getUserData()).remove();
                }
                break;
            case PixClient.HEAD_BIT | PixClient.BRICK_BIT:
            case PixClient.HEAD_BIT | PixClient.COIN_BIT:
                if(fixA.getFilterData().categoryBits == PixClient.HEAD_BIT)
                    ((AbstractTileObject) fixB.getUserData()).onHeadHit();
                else
                    ((AbstractTileObject) fixA.getUserData()).onHeadHit();
                break;
            case PixClient.BULLET_BIT | PixClient.DEFAULT_BIT:
                if(fixA.getFilterData().categoryBits == PixClient.DEFAULT_BIT)
                    ((AbstractBullet) fixB.getUserData()).remove();
                else
                    ((AbstractBullet) fixB.getUserData()).remove();
                break;
            //case PixShooter.ENEMY_HEAD_BIT | PixShooter.PLAYER_BIT:
            //    if(fixA.getFilterData().categoryBits == PixShooter.ENEMY_HEAD_BIT)
            //        ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
            //    else
            //        ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
            //    break;
            //case PixShooter.ENEMY_BIT | PixShooter.OBJECT_BIT:
            //    if(fixA.getFilterData().categoryBits == PixShooter.ENEMY_BIT)
            //        ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
            //    else
            //        ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
            //    break;
            //case PixShooter.PLAYER_BIT | PixShooter.ENEMY_BIT:
            //    if(fixA.getFilterData().categoryBits == PixShooter.PLAYER_BIT)
            //        ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
            //    else
            //        ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
            //    break;
            //case PixShooter.ENEMY_BIT | PixShooter.ENEMY_BIT:
            //    ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
            //    ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
            //    break;
            //case PixShooter.ITEM_BIT | PixShooter.OBJECT_BIT:
            //    if(fixA.getFilterData().categoryBits == PixShooter.ITEM_BIT)
            //        ((Item)fixA.getUserData()).reverseVelocity(true, false);
            //    else
            //        ((Item)fixB.getUserData()).reverseVelocity(true, false);
            //    break;
            //case PixShooter.ITEM_BIT | PixShooter.PLAYER_BIT:
            //    if(fixA.getFilterData().categoryBits == PixShooter.ITEM_BIT)
            //        ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
            //    else
            //        ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
            //    break;
            //case PixShooter.FIREBALL_BIT | PixShooter.OBJECT_BIT:
            //    if(fixA.getFilterData().categoryBits == PixShooter.FIREBALL_BIT)
            //        ((FireBall)fixA.getUserData()).setToDestroy();
            //    else
            //        ((FireBall)fixB.getUserData()).setToDestroy();
            //    break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
