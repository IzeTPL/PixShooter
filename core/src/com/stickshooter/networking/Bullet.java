package com.stickshooter.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixShooter;

/**
 * Created by Marian on 20.03.2016.
 */
public class Bullet{

    private float lifeTime = 1;
    private float lifeTimer = 0;
    public Body body;

    private World world;
    private Player player;
    private OrthographicCamera orthographicCamera;
    private Viewport viewport;
    private float degrees;

    private boolean remove;

    public Bullet(Player player, float degrees) {

        this.player = player;
        this.world = player.getWorld();
        this.orthographicCamera = player.getOrthographicCamera();
        this.viewport = player.getViewport();
        this.degrees = degrees;

        defineBullet();

    }

    private void defineBullet() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(player.body.getPosition().x, player.body.getPosition().y);
        bdef.type = BodyDef.BodyType.KinematicBody;
        bdef.bullet = true;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(PixShooter.downScale(3f));

        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = PixShooter.BULLET_BIT;
        fdef.filter.categoryBits = PixShooter.DEFAULT_BIT;
        body.createFixture(fdef).setUserData("bullet");

        body.setLinearVelocity(new Vector2(1f, 1f).setAngle(degrees));

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
