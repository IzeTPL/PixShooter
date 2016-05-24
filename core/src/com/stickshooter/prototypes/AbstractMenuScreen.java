package com.stickshooter.prototypes;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.stickshooter.PixClient;
import com.stickshooter.tools.FTFontGenerator;

/**
 * Created by Marian on 22.05.2016.
 */
public class AbstractMenuScreen extends AbstractScreen {

    protected PixClient game;
    protected Stage stage;
    protected FTFontGenerator generator;

    public AbstractMenuScreen(PixClient game) {

        super(game);
        this.game = game;
        gameViewport = new FitViewport(PixClient.V_WIDTH, PixClient.V_HEIGHT, orthographicCamera);
        orthographicCamera.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);
        generator = new FTFontGenerator(this.game.manager);
        stage = new Stage(gameViewport, this.game.batch);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        super.render(delta);
        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        super.dispose();
        game.dispose();
        stage.dispose();
        generator.dispose();

    }

}