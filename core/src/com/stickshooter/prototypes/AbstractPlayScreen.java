package com.stickshooter.prototypes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.stickshooter.AbstractGame;
import com.stickshooter.tools.B2WorldCreator;
import com.stickshooter.tools.WorldContactListener;

/**
 * Created by Marian on 16.05.2016.
 */
public abstract class AbstractPlayScreen extends AbstractScreen{

    //mapa
    private TmxMapLoader mapLoader;
    private TiledMap map;
    protected MapProperties mapProperties;
    protected OrthogonalTiledMapRenderer renderer;

    //box2D
    protected World world;
    private Box2DDebugRenderer b2dr;

    public AbstractPlayScreen(AbstractGame game) {

        this.game = game;
        gamecam = new OrthographicCamera();

        gameViewport = new FitViewport(AbstractGame.downScale(AbstractGame.V_WIDTH/ AbstractGame.SCALE), AbstractGame.downScale(AbstractGame.V_HEIGHT / AbstractGame.SCALE), gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        mapProperties = new MapProperties();
        mapProperties = map.getProperties();
        renderer = new OrthogonalTiledMapRenderer(map, 1f/ AbstractGame.PIXELS_PER_METER);
        gamecam.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);


        world = new World(new Vector2(0, -10f), true);

        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        world.setContactListener(new WorldContactListener());

    }
    @Override
    public void show() {

    }

    public void update(float delta) {

        world.step(1/60f, 6, 2);
        gamecam.update();
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {

        super.render(delta);
        renderer.render();
        b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);

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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();

    }

    public World getWorld() {
        return world;
    }

}
