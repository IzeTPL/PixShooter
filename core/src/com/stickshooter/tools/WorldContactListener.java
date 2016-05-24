package com.stickshooter.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.stickshooter.PixClient;
import com.stickshooter.PixServer;
import com.stickshooter.networking.Bullet;
import com.stickshooter.networking.Player;
import com.stickshooter.prototypes.AbstractBullet;
import com.stickshooter.prototypes.AbstractTileObject;

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
            case PixClient.BULLET_BIT | PixServer.CLIENT_BIT:
                if(fixA.getFilterData().categoryBits == PixServer.CLIENT_BIT) {
                    if( !( (Player) fixA.getUserData() ).bullets.contains(((Bullet) fixB.getUserData())) ) {
                        ((Bullet) fixB.getUserData()).getPlayer().addScore(1);
                        ((Bullet) fixB.getUserData()).remove();
                        ((Player) fixA.getUserData()).setDead();
                    }
                } else {
                    if( !( (Player) fixB.getUserData() ).bullets.contains(((Bullet) fixA.getUserData())) ) {
                        ((Bullet) fixA.getUserData()).getPlayer().addScore(1);
                        ((Bullet) fixA.getUserData()).remove();
                        ((Player) fixB.getUserData()).setDead();
                    }
                }
                break;
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
