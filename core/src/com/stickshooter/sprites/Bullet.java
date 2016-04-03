package com.stickshooter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixShooter;

/**
 * Created by Marian on 20.03.2016.
 */
public class Bullet extends Sprite {

    private float lifeTime = 1;
    private float lifeTimer = 0;
    public Body body;

    public World getWorld() {
        return world;
    }

    private World world;
    private Player player;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera orthographicCamera;
    private Viewport viewport;

    private boolean remove;

    public Bullet(World world, Player player, OrthographicCamera orthographicCamera, Viewport viewport) {

        this.world = world;
        this.player = player;
        this.orthographicCamera = orthographicCamera;
        this.viewport = viewport;

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
        shape.setRadius(PixShooter.downScale(3f));

        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = PixShooter.BULLET_BIT;
        fdef.filter.categoryBits = PixShooter.DEFAULT_BIT;
        body.createFixture(fdef).setUserData("bullet");

        body.setLinearVelocity(new Vector2(1f, 1f).setAngle(new Vector2(PixShooter.downScale( (2 * (float)Gdx.input.getX() - (float)Gdx.graphics.getWidth() ) / (2 * PixShooter.SCALE) ) + ((orthographicCamera.position.x - player.body.getPosition().x) * ( (float)viewport.getScreenWidth() / PixShooter.V_WIDTH) ), PixShooter.downScale( ( (float)Gdx.graphics.getHeight() - 2 * (float)Gdx.input.getY() ) / (2 * PixShooter.SCALE) ) + ((orthographicCamera.position.y - player.body.getPosition().y) * ( (float)viewport.getScreenHeight() / PixShooter.V_HEIGHT) )).angle()));

    }

    public boolean shouldRemove() { return remove; }

    public void update(float dt) {

        lifeTimer += dt;
        if(lifeTimer > lifeTime) {
            remove = true;
        }

    }

    public void draw(Matrix4 matrix4) {

        shapeRenderer.setProjectionMatrix(matrix4);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1,1,1,1);
        shapeRenderer.circle(body.getPosition().x, body.getPosition().y, PixShooter.downScale(3f), 100);
        shapeRenderer.end();

    }

}
