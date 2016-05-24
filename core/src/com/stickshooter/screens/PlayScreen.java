package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
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

    //tworzenie screenshotów
    byte[] pixels;

    //elementy ekranu
    private Hud hud;
    private DebugOverlay debugOverlay;
    private PauseMenu pauseMenu;

    private Client client;
    private HashSet<Integer> IDs;
    private HashMap<Integer, Player> players;
    private ShapeRenderer shapeRenderer;

    private SimpleDateFormat simpleDateFormat;

    public PlayScreen(PixClient game, String nickname, String host, int port) {

        super(game);
        this.game = game;
        simpleDateFormat = new SimpleDateFormat ("E_yyyy.MM.dd_'at'_hh-mm-ss_a_zzz");

        shapeRenderer = new ShapeRenderer();
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        client = new Client();

        hud = new Hud(game.batch);
        pauseMenu = new PauseMenu(game.batch, game.manager);

        IDs = new HashSet<>();

        debugOverlay = new DebugOverlay(game.batch);
        players = new HashMap<>();

        client.nickname = nickname;
        client.host = host;
        client.port = port;
        client.connect();

    }

    @Override
    public void show() {

    }

    @Override
    public void update(float dt) {

        super.update(dt);

        if(!client.connected)game.setScreen(new ConnectScreen(game));

        if (players.get(client.ID).body.getPosition().x > gameViewport.getWorldWidth()/2f && players.get(client.ID).body.getPosition().x < PixClient.downScale(PixClient.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldWidth()/2f)
            orthographicCamera.position.x = players.get(client.ID).body.getPosition().x;

        if (players.get(client.ID).body.getPosition().y > gameViewport.getWorldHeight()/2f && players.get(client.ID).body.getPosition().y < PixClient.downScale(PixClient.TILE_SIZE * (float)mapProperties.get("width", Integer.class)) - gameViewport.getWorldHeight()/2f)
            orthographicCamera.position.y = players.get(client.ID).body.getPosition().y;

        for (Integer ID : IDs) {

            players.get(ID).update(dt);

            if(players.get(ID).shouldRemove() ) {

                players.get(ID).getWorld().destroyBody(players.get(ID).body);
                players.get(ID).reset();

            }

        }

        for (Integer ID : IDs) {

        for(int i = 0; i < players.get(ID).bullets.size(); i++) {
            players.get(ID).bullets.get(i).update(dt);
            if (players.get(ID).bullets.get(i).shouldRemove()) {
                players.get(ID).bullets.get(i).getWorld().destroyBody(players.get(ID).bullets.get(i).body);
                players.get(ID).bullets.remove(i);
                i--;
            }
        }

        }

        debugOverlay.update(players.get(client.ID), gameViewport, orthographicCamera);
        toggleMenu(dt);

    }


    @Override
    public void render(float delta) {

        super.render(delta);

        update(delta);

        try {
            handleInput();
        } catch (IOException e) {

            client.closeObjects();
            game.setScreen(new ConnectScreen(game));

        }




        for (Integer ID : IDs) {

            game.batch.begin();
                players.get(ID).sprite.draw(game.batch);
            game.batch.end();

            for(int i = 0; i < players.get(ID).bullets.size(); i++) {
                shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(1, 1, 0, 1);
                shapeRenderer.circle( players.get(ID).bullets.get(i).body.getPosition().x,  players.get(ID).bullets.get(i).body.getPosition().y, PixClient.downScale(1f), 100);
                shapeRenderer.end();
            }

        }

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

        super.resize(width, height);
        hud.stage.getViewport().update(width, height);
        pauseMenu.stage.getViewport().update(width, height);
        debugOverlay.stage.getViewport().update(width, height);

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
        hud.dispose();
        pauseMenu.dispose();
        debugOverlay.dispose();

    }

    public void updatePlayers(float dt) {

        for (Integer ID : IDs) {

            players.get(ID).body.setTransform(client.playersPosition.get(ID).x, client.playersPosition.get(ID).y, 0f);
            players.get(ID).body.setLinearVelocity(client.playersVelocity.get(ID).x, client.playersVelocity.get(ID).y);

        }

    }

    public void handleInput() throws IOException{

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && players.get(client.ID).body.getLinearVelocity().y == 0) {
                client.jump();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && players.get(client.ID).body.getLinearVelocity().x <= 2f) {
                client.moveRight();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && players.get(client.ID).body.getLinearVelocity().x >= -2f) {

                client.moveLeft();

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {

                takeScreenshot();

            }

            if (Gdx.input.justTouched()) {

                client.shoot();

            }

        }

        if (pauseMenu.getMainMenuButton().isPressed() && pauseMenu.isVisible()) {

            client.closeObjects();
            game.setScreen(new MainMenuScreen(game));

        }

    }

    public void toggleMenu(float delta) {

        if(!isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !pauseMenu.isVisible()) {

                pauseMenu.show();
                isPaused = true;

            }

        } else {

            if ((pauseMenu.getPlayButton().isPressed() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) && pauseMenu.isVisible()) {

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

        private int ID;
        private Thread thread = null;
        private Object locker = new Object();

        public String host = null;
        public String nickname = null;
        public int port;

        public boolean connect() {

            if (this.connected) {

                return false;

            }

            try {

                socket = new Socket(host, port);

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeByte(FrameType.LOGIN);

                if (dataInputStream.readBoolean() ) {

                    ID = dataInputStream.readInt();
                    IDs.add(ID);

                    int size = dataInputStream.readInt();

                    for(int i = 0; i < size; i++) {

                        int iD = dataInputStream.readInt();
                        IDs.add(iD);
                        players.put(iD, new Player(PlayScreen.this) );

                    }

                    connected = true;
                    looped = true;

                    runThread();

                    return true;

                } else {

                    closeObjects();
                    game.setScreen(new ConnectScreen(game));

                }

            } catch (IOException e) {

                closeObjects();
                game.setScreen(new ConnectScreen(game));


            }

            return false;

        }

        private void runThread() {

            thread = new Thread( () -> {


                try {

                    while ( looped ) {

                        interpretFrame();

                    }

                } catch (Exception e) {

                    closeObjects();
                    looped = false;
                    connected = false;

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

                case FrameType.PLAYER_DIED:
                    interpretPlayerDied();
                    break;

                case FrameType.PLAYER_RESPAWN:
                    interpretPlayerRespawn();
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
                    IDs.add(iD);

                }

            }
        }

        private void interpretNewPlayer() throws IOException {

            synchronized (locker) {

                int iD = dataInputStream.readInt();
                players.put(iD, new Player(PlayScreen.this) );
                IDs.add(iD);


            }

        }

        private void interpretPlayerLeft() throws IOException {

            synchronized (locker) {

                int iD = dataInputStream.readInt();
                players.get(iD).setDead();
                players.remove(iD);
                IDs.remove(iD);

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

        private void interpretPlayerDied() throws IOException {

            int iD;

            synchronized (locker) {

                iD = dataInputStream.readInt();
                players.get(iD).setDead();

            }

        }

        private void interpretPlayerRespawn() throws IOException {

            int iD;

            synchronized (locker) {

                iD = dataInputStream.readInt();
                players.get(iD).setToRespawn();

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

        public void shoot() throws  IOException {

            synchronized (locker) {

                float degrees = new Vector2(PixClient.downScale( (2 * (float)Gdx.input.getX() - (float)Gdx.graphics.getWidth() ) / (2 * PixClient.SCALE) ) + ((orthographicCamera.position.x - players.get(client.ID).body.getPosition().x) * ( (float)gameViewport.getScreenWidth() / PixClient.V_WIDTH) ), PixClient.downScale( ( (float)Gdx.graphics.getHeight() - 2 * (float)Gdx.input.getY() ) / (2 * PixClient.SCALE) ) + ((orthographicCamera.position.y - players.get(client.ID).body.getPosition().y) * ( (float)gameViewport.getScreenHeight() / PixClient.V_HEIGHT) )).angle();
                dataOutputStream.writeByte(FrameType.SHOOT);
                dataOutputStream.writeFloat(degrees);

            }

        }

        private synchronized void closeObjects()
        {

            closeObject(dataOutputStream);
            closeObject(dataInputStream);
            closeObject(socket);

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
