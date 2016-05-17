package com.stickshooter.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixClient;
import com.stickshooter.prototypes.AbstractBullet;
import com.stickshooter.prototypes.AbstractPlayer;

/**
 * Created by Marian on 20.03.2016.
 */
public class Bullet extends AbstractBullet{

    private ShapeRenderer shapeRenderer;

    public Bullet(Player player, float degrees) {

        super(player, degrees);
        //shapeRenderer = new ShapeRenderer();
        body.setLinearVelocity(new Vector2(1f, 1f).setAngle(degrees));

    }

    public void draw(Matrix4 matrix4) {

        shapeRenderer.setProjectionMatrix(matrix4);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1,1,1,1);
        shapeRenderer.circle(body.getPosition().x, body.getPosition().y, PixClient.downScale(3f), 100);
        shapeRenderer.end();

    }

}
