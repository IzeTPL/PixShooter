package com.stickshooter.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.StickShooter;
import com.stickshooter.sprites.Player;

/**
 * Created by Marian on 20.03.2016.
 */
public class DebugOverlay implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private Label xLabel;
    private Label yLabel;
    private Label fpsLabel;

    private float y = 0;
    private float x = 0;

    public DebugOverlay(SpriteBatch sb) {


        viewport = new FitViewport(StickShooter.V_WIDTH, StickShooter.V_HEIGHT, new OrthographicCamera());

        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.bottom().right();
        table.setFillParent(true);

        fpsLabel = new Label("fps: ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        xLabel = new Label("x = ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        yLabel = new Label("y = ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(fpsLabel);
        table.row();
        table.add(xLabel);
        table.row();
        table.add(yLabel);

        stage.addActor(table);
    }

    public void update(Player player) {

        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
        x = player.body.getPosition().x;
        xLabel.setText("x = " + String.format("%.4f", x));
        y = player.body.getPosition().y;
        yLabel.setText("y = " + String.format("%.4f", y));

    }


    @Override
    public void dispose() {

        stage.dispose();

    }

}
