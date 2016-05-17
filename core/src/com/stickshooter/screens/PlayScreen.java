package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.stickshooter.AbstractGame;
import com.stickshooter.PixClient;
import com.stickshooter.networking.FrameType;
import com.stickshooter.networking.MovementType;
import com.stickshooter.prototypes.AbstractPlayScreen;
import com.stickshooter.scenes.Hud;
import com.stickshooter.scenes.PauseMenu;
import com.stickshooter.sprites.Bullet;
import com.stickshooter.sprites.Player;
import com.stickshooter.tools.DebugOverlay;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Marian on 06.03.2016.
 */
public class PlayScreen extends AbstractPlayScreen{

    private PixClient game;
    private TextureAtlas atlas;
    private boolean isPaused = false;
    private boolean isMultiplayer = false;
    private ShapeRenderer shapeRenderer;

    //tworzenie screenshotów
    byte[] pixels;

    //elementy ekranu
    private Hud hud;
    private DebugOverlay debugOverlay;
    private PauseMenu pauseMenu;

    private Client client;
    private HashSet<Integer> iDs;
    private HashMap<Integer, Player> players;
    private Player player;

    SimpleDateFormat simpleDateFormat;

    public PlayScreen(AbstractGame game) {

        super(game);
        simpleDateFormat = new SimpleDateFormat ("E_yyyy.MM.dd_'at'_hh-mm-ss_a_zzz");

        shapeRenderer = new ShapeRenderer();
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        client = new Client();

        this.game = (PixClient) game;

        hud = new Hud(game.batch);
        pauseMenu = new PauseMenu(game.batch, game.manager);

        iDs = new HashSet<>();

        debugOverlay = new DebugOverlay(game.batch);
        players = new HashMap<>();

        client.connect("Player");

       //player = new Player(this);

    }

    @Override
    public void show() {

    }

    @Override
    public void update(float dt) {

        super.update(dt);

        if (players.get(client.userId).body.getPosition().x > gameViewport.getWorldWidth()/2f && players.get(client.userId).body.getPosition().x < PixClient.downScale(PixClient.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldWidth()/2f)
            gamecam.position.x = players.get(client.userId).body.getPosition().x;

        if (players.get(client.userId).body.getPosition().y > gameViewport.getWorldHeight()/2f && players.get(client.userId).body.getPosition().y < PixClient.downScale(PixClient.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldHeight()/2f)
            gamecam.position.y = players.get(client.userId).body.getPosition().y;

        for (Integer iD : iDs) {

            players.get(iD).update(dt);

        }

        debugOverlay.update(players.get(client.userId), gameViewport, gamecam);

        try {
            handleInput(dt);
        } catch (IOException e) {}


        toggleMenu(dt);

        //player.update(dt);
//
        //if (player.body.getPosition().x > gameViewport.getWorldWidth()/2f && player.body.getPosition().x < PixClient.downScale(PixClient.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldWidth()/2f)
        //    gamecam.position.x = player.body.getPosition().x;
//
        //if (player.body.getPosition().y > gameViewport.getWorldHeight()/2f && player.body.getPosition().y < PixClient.downScale(PixClient.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldHeight()/2f)
        //    gamecam.position.y = player.body.getPosition().y;
//
        //debugOverlay.update(player, gameViewport, gamecam);
//
        //for(int i = 0; i < player.bullets.size(); i++) {
        //    player.bullets.get(i).update(dt);
        //    if(player.bullets.get(i).shouldRemove()) {
        //        player.bullets.get(i).getWorld().destroyBody(player.bullets.get(i).body);
        //        player.bullets.remove(i);
        //        i--;
        //    }
        //}
//
        //hud.update(dt);

    }


    @Override
    public void render(float delta) {

        super.render(delta);

        update(delta);

        game.batch.begin();


        for (Integer iD : iDs) {

            players.get(iD).sprite.draw(game.batch);

        }


        //player.sprite.draw(game.batch);
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

        super.resize(width, height);
        hud.stage.getViewport().update(width, height);
        pauseMenu.stage.getViewport().update(width, height);
        debugOverlay.stage.getViewport().update(width, height);

    }

    @Override
    public void pause() {
        //players.get(client.userId).body.setActive(false);
    }

    @Override
    public void resume() {
        //players.get(client.userId).body.setActive(true);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        super.dispose();
        hud.dispose();
        pauseMenu.dispose();
        debugOverlay.dispose();

    }

    public void updatePlayers(float dt) {

            for (Integer iD : iDs) {

                players.get(iD).body.setTransform(client.playersPosition.get(iD).x, client.playersPosition.get(iD).y, 0f);
                players.get(iD).body.setLinearVelocity(client.playersVelocity.get(iD).x, client.playersVelocity.get(iD).y);

            }

            //for(int i = 0; i < player.bullets.size(); i++) {
            //    player.bullets.get(i).update(dt);
            //    if(player.bullets.get(i).shouldRemove()) {
            //        player.bullets.get(i).getWorld().destroyBody(player.bullets.get(i).body);
            //        player.bullets.remove(i);
            //        i--;
            //    }
            //}


    }

    public void handleInput(float dt) throws IOException{

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && players.get(client.userId).body.getLinearVelocity().y == 0) {
                client.jump();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && players.get(client.userId).body.getLinearVelocity().x <= 2f) {
                client.moveRight();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && players.get(client.userId).body.getLinearVelocity().x >= -2f) {

                client.moveLeft();

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {

                takeScreenshot();

            }

            if (Gdx.input.justTouched()) {

                client.shoot(new Vector2(PixClient.downScale( (2 * (float)Gdx.input.getX() - (float)Gdx.graphics.getWidth() ) / (2 * PixClient.SCALE) ) + ((gamecam.position.x - players.get(client.userId).body.getPosition().x) * ( (float)gameViewport.getScreenWidth() / PixClient.V_WIDTH) ), PixClient.downScale( ( (float)Gdx.graphics.getHeight() - 2 * (float)Gdx.input.getY() ) / (2 * PixClient.SCALE) ) + ((gamecam.position.y - players.get(client.userId).body.getPosition().y) * ( (float)gameViewport.getScreenHeight() / PixClient.V_HEIGHT) )).angle());

            }

        }

        //if(!isPaused) {
//
        //    if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.body.getLinearVelocity().y == 0) {
//
        //        player.body.applyLinearImpulse(new Vector2(0, 4f), player.body.getWorldCenter(), true);
//
        //    }
//
        //    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2f) {
//
        //        player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);
//
        //    }
//
        //    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2f) {
//
        //        player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);
//
        //    }
//
        //    if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
//
        //        takeScreenshot();
//
        //    }
//
        //    if (Gdx.input.justTouched()) {
//
        //        player.shoot();
//
        //    }
//
        //}

        if (pauseMenu.getMainMenuButton().isPressed() && pauseMenu.isVisible()) {

            game.setScreen(new MainMenuScreen(game));

        }

    }

    public void toggleMenu(float delta) {

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

    public void takeScreenshot() {

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external("Screenshots/Screenshot_" + simpleDateFormat.format(new Date()) + ".png"), pixmap);
        pixmap.dispose();

    }

    public TextureAtlas getAtlas() {
        return atlas;
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

                case FrameType.PLAYER_LEFT:
                    interpretPlayerLeft();
                    break;

                case FrameType.PLAYER_SHOT:
                    interpretPlayerShot();
                    break;

                default:
                    break;

            }

        }

        private void interpretSynchronize() throws Exception{

            HashMap<Integer, Vector2> playersPosition = new HashMap<>();
            HashMap<Integer, Vector2> playersVelocity = new HashMap<>();
            int iD;

                for(int i = 0; i < players.size(); i++) {

                    Vector2 position = new Vector2();
                    Vector2 velocity = new Vector2();

                    synchronized (locker) {
                        iD = dataInputStream.readInt();

                        position.x = dataInputStream.readFloat();
                        position.y = dataInputStream.readFloat();

                        velocity.x = dataInputStream.readFloat();
                        velocity.y = dataInputStream.readFloat();
                    }

                    playersVelocity.put(iD, velocity);
                    playersPosition.put(iD, position);

                }

                this.playersPosition = playersPosition;
                this.playersVelocity = playersVelocity;

            updatePlayers(Gdx.graphics.getDeltaTime());

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

        private void interpretPlayerLeft() throws IOException {

            synchronized (locker) {

                int iD = dataInputStream.readInt();
                players.remove(iD);
                iDs.remove(iD);

            }

        }

        private void interpretPlayerShot() throws IOException {

            int iD;
            float degrees;

            synchronized (locker) {

                iD = dataInputStream.readInt();
                degrees = dataInputStream.readFloat();
                players.get(iD).bullets.add(new Bullet(players.get(iD), degrees) );

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
