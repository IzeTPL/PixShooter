package com.stickshooter.prototypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.AbstractGame;

/**
 * Created by Marian on 16.05.2016.
 */
public abstract class AbstractScreen implements Screen {

    protected AbstractGame game;
    protected OrthographicCamera orthographicCamera;
    protected Viewport gameViewport;

    public AbstractScreen(AbstractGame game) {

        this.game = game;
        orthographicCamera = new OrthographicCamera();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    }

    @Override
    public void resize(int width, int height) {

        gameViewport.update(width, height);

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
        game.dispose();
    }

    public OrthographicCamera getOrthographicCamera() {
        return orthographicCamera;
    }

    public Viewport getGameViewport() {
        return gameViewport;
    }

}
