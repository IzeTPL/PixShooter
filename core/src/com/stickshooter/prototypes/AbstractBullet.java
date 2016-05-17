package com.stickshooter.prototypes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixClient;
import com.stickshooter.sprites.Player;

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

    public AbstractBullet(AbstractPlayer player, float degrees) {

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
        shape.setRadius(PixClient.downScale(3f));

        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = PixClient.BULLET_BIT;
        fdef.filter.maskBits = PixClient.DEFAULT_BIT | PixClient.BRICK_BIT | PixClient.OBJECT_BIT;
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

}
