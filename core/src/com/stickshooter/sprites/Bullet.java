package com.stickshooter.sprites;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.stickshooter.StickShooter;

/**
 * Created by Marian on 20.03.2016.
 */
public class Bullet {

    private float lifeTime;
    private float lifeTimer;
    public Body body;
    private World world;
    private Player player;
    private ShapeRenderer shapeRenderer;

    private boolean remove;

    public Bullet(World world, Player player) {

        this.world = world;
        this.player = player;

        defineBullet();

    }

    private void defineBullet() {

        shapeRenderer = new ShapeRenderer();

        BodyDef bdef = new BodyDef();
        bdef.position.set(player.body.getPosition().x, player.body.getPosition().y);
        bdef.type = BodyDef.BodyType.KinematicBody;
        bdef.bullet = true;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3/StickShooter.PPM);

        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef);

        body.setLinearVelocity(new Vector2(0.5f, 0.5f));

    }

    public boolean shouldRemove() { return remove; }

    public void update(float dt) {

        lifeTimer += dt;
        if(lifeTimer > lifeTime) {
            remove = true;
        }

    }

    public void draw(ShapeRenderer shapeRenderer) {

        shapeRenderer.setColor(1,1,1,1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(player.body.getPosition().x,player.body.getPosition().y,30/StickShooter.PPM);
        shapeRenderer.end();


    }

}
