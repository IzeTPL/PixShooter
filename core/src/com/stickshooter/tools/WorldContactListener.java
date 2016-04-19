package com.stickshooter.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.stickshooter.networking.Bullet;
import com.stickshooter.sprites.InteractiveTileObject;

/**
 * Created by Marian on 10.03.2016.
 */
public class WorldContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if(fixA.getUserData() == "head" || fixB.getUserData() == "head") {

            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if(object.getUserData() instanceof InteractiveTileObject) {

                ( (InteractiveTileObject)object.getUserData() ).onHeadHit();

            }

        }

        if(fixA.getUserData() == "bullet" || fixB.getUserData() == "bullet") {

            Fixture bullet = fixA.getUserData() == "bullet" ? fixA : fixB;
            Fixture object = bullet == fixA ? fixB : fixA;

            if ( contact.isTouching() ) {

                //( () ).remove();

            }

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
