package com.stickshooter.prototypes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.AbstractGame;

/**
 * Created by Marian on 16.05.2016.
 */
public abstract class AbstractBullet {

    protected float lifeTime = 1;
    protected float lifeTimer = 0;
    public Body body;

    protected World world;
    protected AbstractPlayer player;
    protected OrthographicCamera orthographicCamera;
    protected Viewport viewport;

    protected boolean remove;

    public AbstractBullet(AbstractPlayer player) {

        this.player = player;
        this.world = player.getWorld();
        this.orthographicCamera = player.getOrthographicCamera();
        this.viewport = player.getViewport();

        defineBullet();

    }

    protected void defineBullet() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(player.body.getPosition().x, player.body.getPosition().y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.bullet = true;
        bdef.gravityScale = 0;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(AbstractGame.downScale(1f));

        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = AbstractGame.BULLET_BIT;
        fdef.filter.maskBits = AbstractGame.DEFAULT_BIT | AbstractGame.BRICK_BIT | AbstractGame.OBJECT_BIT | AbstractGame.CLIENT_BIT;
        body.createFixture(fdef).setUserData(this);

    }

    public boolean shouldRemove() { return remove; }

    public void update(float dt) {

        lifeTimer += dt;
        if(lifeTimer > lifeTime) {
            remove();
        }

    }

    public void remove() {

        remove = true;

    }

    public World getWorld() {
        return world;
    }

    public AbstractPlayer getPlayer() { return player; }

}
