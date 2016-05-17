package com.stickshooter.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixClient;
import com.stickshooter.sprites.Player;

/**
 * Created by Marian on 20.03.2016.
 */
public class DebugOverlay implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private Label[] labels;
    private final int labelCount = 12;

    //private dS = PixShooter.downScale();

    public DebugOverlay(SpriteBatch sb) {


        viewport = new FitViewport(PixClient.V_WIDTH, PixClient.V_HEIGHT, new OrthographicCamera());

        labels = new Label[labelCount];

        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.bottom().right();
        table.setHeight(300);
        table.setWidth(200);
        table.setPosition(Gdx.graphics.getWidth() - table.getWidth(), 0);



        for(int i = 0; i < labelCount; i++) {

            labels[i] = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            table.add(labels[i]).right();
            table.row();

        }

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.75f);
        pixmap.fill();

        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap) ) ) );
        stage.addActor(table);
    }

    public void update(Player player, Viewport viewport, OrthographicCamera orthographicCamera) {

        float x, y, angle;

        labels[0].setText("fps: " + Gdx.graphics.getFramesPerSecond());

        x = player.body.getPosition().x;
        labels[1].setText("x = " + String.format("%.2f", x));

        y = player.body.getPosition().y;
        labels[2].setText("y = " + String.format("%.2f", y));

        x = PixClient.downScale( (2 * (float)Gdx.input.getX() - (float)Gdx.graphics.getWidth() ) / (2 * PixClient.SCALE) ) + ((orthographicCamera.position.x - player.body.getPosition().x) * ( (float)viewport.getScreenWidth() / PixClient.V_WIDTH) );
        labels[3].setText("Mouse according to x = " + String.format("%.2f", x));

        y = PixClient.downScale( ( (float)Gdx.graphics.getHeight() - 2 * (float)Gdx.input.getY() ) / (2 * PixClient.SCALE) ) + ((orthographicCamera.position.y - player.body.getPosition().y) * ( (float)viewport.getScreenHeight() / PixClient.V_HEIGHT) );
        labels[4].setText("Mouse according to y = " + String.format("%.2f", y));

        angle = new Vector2(x, y).angle();
        labels[5].setText("Angle = " + String.format("%.2f", angle));

        x = orthographicCamera.position.x;
        labels[6].setText("cam x = " + String.format("%.2f", x));

        y = orthographicCamera.position.y;
        labels[7].setText("cam y = " + String.format("%.2f", y));

        x = orthographicCamera.position.x - player.body.getPosition().x;
        labels[8].setText("(cam - pos)x = " + String.format("%.2f", x));

        y = orthographicCamera.position.y - player.body.getPosition().y;
        labels[9].setText("(cam - pos)y = " + String.format("%.2f", y));

        x = PixClient.downScale( (2 * Gdx.input.getX() - (Gdx.graphics.getWidth() - viewport.getScreenWidth() ) ) / (2 * PixClient.SCALE) );
        labels[10].setText("Mouse x = " + String.format("%.2f", x));

        y = PixClient.downScale( ( ( (float)Gdx.graphics.getHeight() - Gdx.input.getY() ) / PixClient.SCALE )  - (Gdx.graphics.getHeight() - viewport.getScreenHeight()) / (2 * PixClient.SCALE) );
        labels[11].setText("Mouse y = " + String.format("%.2f", y));

    }


    @Override
    public void dispose() {

        stage.dispose();

    }

}
