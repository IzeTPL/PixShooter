package com.stickshooter.scenes;

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

/**
 * Created by Marian on 06.03.2016.
 */

public class Hud implements Disposable{

    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    private Integer ammo;

    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldsLabel;
    private Label stickmanLabel;

    public Hud(SpriteBatch sb) {

        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(StickShooter.V_WIDTH, StickShooter.V_HEIGHT, new OrthographicCamera());

        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("Time", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldsLabel = new Label("Map", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        stickmanLabel = new Label("Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(stickmanLabel).expandX().padTop(10);
        table.add(worldsLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt) {

        timeCount += dt;
        if(timeCount >= 1) {

            worldTimer--;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;

        }

    }

    public static void addScore(int value) {

        score += value;
        scoreLabel.setText(String.format("%06d", score));

    }

    @Override
    public void dispose() {

        stage.dispose();

    }
}
