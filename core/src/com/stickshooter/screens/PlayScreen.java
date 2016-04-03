package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixShooter;
import com.stickshooter.scenes.Hud;
import com.stickshooter.scenes.PauseMenu;
import com.stickshooter.sprites.Bullet;
import com.stickshooter.sprites.Player;
import com.stickshooter.tools.B2WorldCreator;
import com.stickshooter.tools.DebugOverlay;
import com.stickshooter.tools.WorldContactListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Marian on 06.03.2016.
 */
public class PlayScreen implements Screen {

    private PixShooter game;
    private TextureAtlas atlas;
    private boolean isPaused = false;
    private ShapeRenderer shapeRenderer;

    //tworzenie screenshotów
    byte[] pixels;

    //elementy ekranu
    private OrthographicCamera gamecam;
    private Hud hud;
    private DebugOverlay debugOverlay;
    private PauseMenu pauseMenu;
    private Viewport gameViewport;

    //mapa
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private MapProperties mapProperties;
    private OrthogonalTiledMapRenderer renderer;

    //box2D
    private World world;
    private Box2DDebugRenderer b2dr;

    private Player player;

    SimpleDateFormat simpleDateFormat;


    public PlayScreen(PixShooter game) {

        simpleDateFormat = new SimpleDateFormat ("E_yyyy.MM.dd_'at'_hh-mm-ss_a_zzz");

        shapeRenderer = new ShapeRenderer();
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        gamecam = new OrthographicCamera();

        gameViewport = new FitViewport(PixShooter.downScale(PixShooter.V_WIDTH/PixShooter.SCALE), PixShooter.downScale(PixShooter.V_HEIGHT/PixShooter.SCALE), gamecam);

        hud = new Hud(game.batch);
        pauseMenu = new PauseMenu(game.batch, game.manager);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("test.tmx");
        mapProperties = new MapProperties();
        mapProperties = map.getProperties();
        renderer = new OrthogonalTiledMapRenderer(map, 1f/PixShooter.PIXELS_PER_METER);
        gamecam.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);


        world = new World(new Vector2(0, -10f), true);
        player = new Player(world, this, gamecam, gameViewport);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        world.setContactListener(new WorldContactListener());

        debugOverlay = new DebugOverlay(game.batch);


    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.body.getLinearVelocity().y == 0) {

                player.body.applyLinearImpulse(new Vector2(0, 4f), player.body.getWorldCenter(), true);

            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2f) {

                player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);

            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2f) {

                player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {

                Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
                pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
                BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
                PixmapIO.writePNG(Gdx.files.external("Screenshots/Screenshot_" + simpleDateFormat.format(new Date()) + ".png"), pixmap);
                pixmap.dispose();

            }

        }

        if (pauseMenu.getMainMenuButton().isPressed() && pauseMenu.isVisible()) {

            game.setScreen(new MainMenuScreen(game));

        }

        if (Gdx.input.justTouched()) {

            player.shoot();

        }

    }

    public void toggleMenu(float dt) {

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !pauseMenu.isVisible()) {

                pause();
                pauseMenu.show();
                isPaused = true;

            }

        } else {

            if ((pauseMenu.getPlayButton().isPressed() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) && pauseMenu.isVisible()) {

                resume();
                pauseMenu.hide();
                isPaused = false;

            }
        }

    }

    public void update(float dt) {

        handleInput(dt);
        toggleMenu(dt);

        world.step(1/60f, 6, 2);

        player.update(dt);
        hud.update(dt);
        debugOverlay.update(player, gameViewport, gamecam);

        if (player.body.getPosition().x > gameViewport.getWorldWidth()/2f && player.body.getPosition().x < PixShooter.downScale(PixShooter.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldWidth()/2f)
            gamecam.position.x = player.body.getPosition().x;

        if (player.body.getPosition().y > gameViewport.getWorldHeight()/2f && player.body.getPosition().y < PixShooter.downScale(PixShooter.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldHeight()/2f)
            gamecam.position.y = player.body.getPosition().y;

        gamecam.update();
        renderer.setView(gamecam);

        for(int i = 0; i < player.bullets.size(); i++) {
            player.bullets.get(i).update(dt);
            if(player.bullets.get(i).shouldRemove()) {
                player.bullets.get(i).getWorld().destroyBody(player.bullets.get(i).body);
                player.bullets.remove(i);
                i--;
            }
        }

    }


    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        //player.draw(game.batch);
        game.batch.end();

        shapeRenderer.setProjectionMatrix(gamecam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        for(int i = 0; i < player.bullets.size(); i++) {
            player.bullets.get(i).draw(gamecam.combined);
        }
        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        game.batch.setProjectionMatrix(pauseMenu.stage.getCamera().combined);
        pauseMenu.stage.act();
        pauseMenu.stage.draw();

        game.batch.setProjectionMatrix(debugOverlay.stage.getCamera().combined);
        debugOverlay.stage.draw();

    }

    @Override
    public void resize(int width, int height) {

        gameViewport.update(width, height);
        hud.stage.getViewport().update(width, height);
        pauseMenu.stage.getViewport().update(width, height);
        debugOverlay.stage.getViewport().update(width, height);

    }

    @Override
    public void pause() {
        player.body.setActive(false);
    }

    @Override
    public void resume() {
        player.body.setActive(true);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        pauseMenu.dispose();
        debugOverlay.dispose();

    }

}
