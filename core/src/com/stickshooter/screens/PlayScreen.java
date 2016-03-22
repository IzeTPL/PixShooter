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
import com.stickshooter.StickShooter;
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

    private StickShooter game;
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
    private ArrayList<Bullet> bullets;

    SimpleDateFormat simpleDateFormat;


    public PlayScreen(StickShooter game) {

        simpleDateFormat = new SimpleDateFormat ("E_yyyy.MM.dd_'at'_hh-mm-ss_a_zzz");

        bullets = new ArrayList<Bullet>();
        shapeRenderer = new ShapeRenderer();
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        gamecam = new OrthographicCamera();
        gameViewport = new FitViewport(StickShooter.V_WIDTH/StickShooter.PPM, StickShooter.V_HEIGHT/StickShooter.PPM, gamecam);
        hud = new Hud(game.batch);
        pauseMenu = new PauseMenu(game.batch, game.manager);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("test.tmx");
        mapProperties = new MapProperties();
        mapProperties = map.getProperties();
        renderer = new OrthogonalTiledMapRenderer(map, 1/StickShooter.PPM);
        gamecam.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);


        world = new World(new Vector2(0, -10), true);
        player = new Player(world, this, bullets);
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

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2) {

                player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);

            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2) {

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

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
        debugOverlay.update(player);

        if (player.body.getPosition().x > (16f*(12.5f))/StickShooter.PPM && player.body.getPosition().x < (16*((float)mapProperties.get("width", Integer.class)-(12.5f))/StickShooter.PPM))
            gamecam.position.x = player.body.getPosition().x;

        if (player.body.getPosition().y > (16f*(7.5f))/StickShooter.PPM && player.body.getPosition().y < (16*((float)mapProperties.get("height", Integer.class)-(6.5f))/StickShooter.PPM))
            gamecam.position.y = player.body.getPosition().y;

        gamecam.update();
        renderer.setView(gamecam);

        for(int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update(dt);
            if(bullets.get(i).shouldRemove()) {
                bullets.get(i);
                bullets.remove(i);
                i--;
            }
        }

        for(int i = 0; i < bullets.size(); i++) {

            bullets.get(i).update(dt);

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
        player.draw(game.batch);
        game.batch.end();

        shapeRenderer.setProjectionMatrix(gamecam.combined);
        shapeRenderer.setColor(1,1,1,1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < bullets.size(); i++) {
            shapeRenderer.circle(bullets.get(i).body.getPosition().x, bullets.get(i).body.getPosition().y, 300000000);
        }
        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        game.batch.setProjectionMatrix(pauseMenu.stage.getCamera().combined);
        pauseMenu.stage.act();
        pauseMenu.stage.draw();

        game.batch.setProjectionMatrix(debugOverlay.stage.getCamera().combined);
        debugOverlay.stage.draw();

        for(int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(shapeRenderer);
        }

    }

    @Override
    public void resize(int width, int height) {

        gameViewport.update(width, height);
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
