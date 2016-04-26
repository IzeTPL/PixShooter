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
import com.stickshooter.networking.FrameType;
import com.stickshooter.networking.MovementType;
import com.stickshooter.scenes.Hud;
import com.stickshooter.scenes.PauseMenu;
import com.stickshooter.sprites.Player;
import com.stickshooter.tools.B2WorldCreator;
import com.stickshooter.tools.DebugOverlay;
import com.stickshooter.tools.WorldContactListener;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Marian on 06.03.2016.
 */
public class PlayScreen implements Screen {

    private PixShooter game;
    private TextureAtlas atlas;
    private boolean isPaused = false;
    private boolean isMultiplayer = false;
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

    private Client client;
    private HashMap<Integer, Player> players;
    private HashSet<Integer> iDs;

    SimpleDateFormat simpleDateFormat;

    public PlayScreen(PixShooter game) {

        simpleDateFormat = new SimpleDateFormat ("E_yyyy.MM.dd_'at'_hh-mm-ss_a_zzz");

        shapeRenderer = new ShapeRenderer();
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        client = new Client();

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

        players = new HashMap<>();
        iDs = new HashSet<>();

        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        world.setContactListener(new WorldContactListener());

        debugOverlay = new DebugOverlay(game.batch);

        client.connect("Player");

    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) throws IOException{

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && players.get(client.userId).body.getLinearVelocity().y == 0) {

                //player.body.applyLinearImpulse(new Vector2(0, 4f), player.body.getWorldCenter(), true);
                client.jump();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && players.get(client.userId).body.getLinearVelocity().x <= 2f) {

                //player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);
                client.moveRight();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && players.get(client.userId).body.getLinearVelocity().x >= -2f) {

                //player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);
                client.moveLeft();

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {

                Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
                pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
                BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
                PixmapIO.writePNG(Gdx.files.external("Screenshots/Screenshot_" + simpleDateFormat.format(new Date()) + ".png"), pixmap);
                pixmap.dispose();

            }

            if (Gdx.input.justTouched()) {

                client.shoot(new Vector2(PixShooter.downScale( (2 * (float)Gdx.input.getX() - (float)Gdx.graphics.getWidth() ) / (2 * PixShooter.SCALE) ) + ((gamecam.position.x - players.get(client.userId).body.getPosition().x) * ( (float)gameViewport.getScreenWidth() / PixShooter.V_WIDTH) ), PixShooter.downScale( ( (float)Gdx.graphics.getHeight() - 2 * (float)Gdx.input.getY() ) / (2 * PixShooter.SCALE) ) + ((gamecam.position.y - players.get(client.userId).body.getPosition().y) * ( (float)gameViewport.getScreenHeight() / PixShooter.V_HEIGHT) )).angle());
                //player.shoot();

            }

        }

        if (pauseMenu.getMainMenuButton().isPressed() && pauseMenu.isVisible()) {

            game.setScreen(new MainMenuScreen(game));

        }

    }

    public void toggleMenu(float dt) {

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !pauseMenu.isVisible()) {

                if (!isMultiplayer) pause();
                pauseMenu.show();
                isPaused = true;

            }

        } else {

            if ((pauseMenu.getPlayButton().isPressed() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) && pauseMenu.isVisible()) {

                if (!isMultiplayer) resume();
                pauseMenu.hide();
                isPaused = false;

            }
        }

    }

    public void update(float dt) throws IOException{

        handleInput(dt);
        toggleMenu(dt);

        world.step(1/60f, 6, 2);

        if (!players.isEmpty() ) {

            for (Integer iD : iDs) {

                players.get(iD).body.setTransform(client.playersPosition.get(iD).x, client.playersPosition.get(iD).y, 0f);
                players.get(iD).body.setLinearVelocity(client.playersVelocity.get(iD).x, client.playersVelocity.get(iD).y);
                players.get(iD).update(dt);

            }

            if (players.get(client.userId).body.getPosition().x > gameViewport.getWorldWidth()/2f && players.get(client.userId).body.getPosition().x < PixShooter.downScale(PixShooter.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldWidth()/2f)
                gamecam.position.x = players.get(client.userId).body.getPosition().x;

            if (players.get(client.userId).body.getPosition().y > gameViewport.getWorldHeight()/2f && players.get(client.userId).body.getPosition().y < PixShooter.downScale(PixShooter.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldHeight()/2f)
                gamecam.position.y = players.get(client.userId).body.getPosition().y;

            debugOverlay.update(players.get(client.userId), gameViewport, gamecam);

            //for(int i = 0; i < player.bullets.size(); i++) {
            //    player.bullets.get(i).update(dt);
            //    if(player.bullets.get(i).shouldRemove()) {
            //        player.bullets.get(i).getWorld().destroyBody(player.bullets.get(i).body);
            //        player.bullets.remove(i);
            //        i--;
            //    }
            //}

        }

        hud.update(dt);

        gamecam.update();
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {

        try {

            update(delta);

        } catch (IOException e) {

            e.printStackTrace();

        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        if(!players.isEmpty() ) {

            for (Integer iD : iDs) {

                players.get(iD).sprite.draw(game.batch);

            }

        }

        game.batch.end();

        shapeRenderer.setProjectionMatrix(gamecam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);

        //for(int i = 0; i < player.bullets.size(); i++) {
        //    player.bullets.get(i).draw(gamecam.combined);
        //}

        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        //hud.stage.draw();
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
        players.get(client.userId).body.setActive(false);
    }

    @Override
    public void resume() {
        players.get(client.userId).body.setActive(true);
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

    public World getWorld() {
        return world;
    }

    public OrthographicCamera getGamecam() {
        return gamecam;
    }

    public Viewport getGameViewport() {
        return gameViewport;
    }

    private class Client{

        public HashMap<Integer, Vector2> playersPosition;
        public HashMap<Integer, Vector2> playersVelocity;

        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        private Socket socket = null;

        private boolean connected = false;
        private boolean looped = false;

        private int userId;
        private String userLogin;
        private Thread thread = null;
        private Object locker = new Object();

        public boolean connect(String login) {

            if (this.connected) {

                return false;

            }

            try {

                socket = new Socket("localhost", 1337);

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeByte(FrameType.LOGIN);

                if (dataInputStream.readBoolean() ) {

                    userId = dataInputStream.readInt();
                    iDs.add(userId);

                    int size = dataInputStream.readInt();

                    for(int i = 0; i < size; i++) {

                        int iD = dataInputStream.readInt();
                        iDs.add(iD);
                        players.put(iD, new Player(PlayScreen.this) );

                    }

                    connected = true;
                    looped = true;

                    runThread();

                    return true;

                } else {

                    closeObjects();

                }

            } catch (IOException e) {

                e.printStackTrace();

            }

            return false;

        }

        private void runThread() {

            thread = new Thread( () -> {

                boolean error = false;

                try {

                    while ( looped || true ) {

                        interpretFrame();

                    }

                } catch (Exception e) {

                    error = true;

                } finally {

                    closeObjects();
                    looped = false;
                    connected = false;

                }

            });

            thread.setName("Client-logic-thread");
            thread.start();

        }

        private void interpretFrame() throws Exception {

            int frameType = dataInputStream.readByte();

            switch (frameType) {

                case FrameType.SYNCHRONIZE:
                    interpretSynchronize();
                    break;

                case FrameType.LOGIN:
                    interpretLogin();
                    break;

                case FrameType.NEW_PLAYER:
                    interpretNewPlayer();
                    break;

                default:
                    break;

            }

        }

        private void interpretSynchronize() throws Exception{

            synchronized (locker) {

                HashMap<Integer, Vector2> playersPosition = new HashMap<>();
                HashMap<Integer, Vector2> playersVelocity = new HashMap<>();

                for(int i = 0; i < players.size(); i++) {

                    int iD = dataInputStream.readInt();
                    Vector2 position = new Vector2();
                    Vector2 velocity = new Vector2();

                    position.x = dataInputStream.readFloat();
                    position.y = dataInputStream.readFloat();
                    playersPosition.put(iD, position);

                    velocity.x = dataInputStream.readFloat();
                    velocity.y = dataInputStream.readFloat();
                    playersVelocity.put(iD, velocity);

                }

                this.playersPosition = playersPosition;
                this.playersVelocity = playersVelocity;

            }

        }

        private void interpretLogin() throws IOException {

            synchronized (locker) {

                int size = dataInputStream.readInt();

                for(int i = 0; i < size; i++) {

                    int iD = dataInputStream.readInt();
                    players.put(iD, new Player(PlayScreen.this) );
                    iDs.add(iD);

                }

            }
        }

        private void interpretNewPlayer() throws IOException {

            synchronized (locker) {

                int iD = dataInputStream.readInt();
                players.put(iD, new Player(PlayScreen.this) );
                iDs.add(iD);


            }

        }

        public void jump() throws IOException{

            synchronized (locker) {

                dataOutputStream.writeByte(FrameType.MOVE);
                dataOutputStream.writeByte(MovementType.JUMP);

            }

        }

        public void moveRight() throws IOException{

            synchronized (locker) {

                dataOutputStream.writeByte(FrameType.MOVE);
                dataOutputStream.writeByte(MovementType.RIGHT);

            }

        }

        public void moveLeft() throws IOException{

            synchronized (locker) {

                dataOutputStream.writeByte(FrameType.MOVE);
                dataOutputStream.writeByte(MovementType.LEFT);

            }

        }

        public void shoot(float degrees) throws  IOException {

            synchronized (locker) {

                dataOutputStream.writeByte(FrameType.SHOOT);
                dataOutputStream.writeFloat(degrees);

            }

        }

        private synchronized void closeObjects()
        {

            closeObject(dataOutputStream);
            closeObject(dataInputStream);

        }

        private boolean closeObject( Closeable object ) {

            if (object == null) {

                return false;

            }

            try {

                    object.close();
                    return true;

            } catch (IOException e) {

                    return false;

            }

        }

    }

}
