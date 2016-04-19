package com.stickshooter.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixServer;
import com.stickshooter.PixShooter;
import com.stickshooter.tools.B2WorldCreator;
import com.stickshooter.tools.WorldContactListener;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marian on 08.04.2016.
 */
public class Server implements Screen{

    private PixServer game;

    //screen stuff
    private Viewport viewport;
    private OrthographicCamera orthographicCamera;

    //map
    private TmxMapLoader mapLoader;
    private TiledMap tiledMap;
    private MapProperties mapProperties;

    //box2D physics
    private World world;
    private Box2DDebugRenderer b2dr;
    private HashMap<Integer, Player> players;

    //server elements and logic
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private boolean connected = false;
    private boolean looped = false;
    private Thread thread;
    private Object locker = new Object();
    private HashSet<DataOutputStream> dataOutputStreams = new HashSet<>();
    private HashMap<DataOutputStream, Integer> iDs;


    public Server(PixServer game) {

        this.game = game;

        orthographicCamera = new OrthographicCamera();
        viewport = new FitViewport(PixShooter.downScale(PixServer.V_WIDTH/PixServer.SCALE), PixShooter.downScale(PixServer.V_HEIGHT/PixServer.SCALE), orthographicCamera);

        mapLoader = new TmxMapLoader();
        tiledMap = mapLoader.load("test.tmx");
        mapProperties = new MapProperties();
        mapProperties = tiledMap.getProperties();
        orthographicCamera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        world = new World(new Vector2(0, -10f), true);

        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, tiledMap);


        world.setContactListener(new WorldContactListener());

        players = new HashMap<>();
        iDs = new HashMap<>();

        create();

    }

    public boolean create() {

        if (connected) return false;

        try
        {
            serverSocket = new ServerSocket( 1337 );
        }
        catch ( IOException e )
        {
            return false;
        }

        // setup a server thread where we wait for incoming connections
        // to the server
        thread = new Thread( ( ) -> {

                while(true) {

                    try {

                        socket = serverSocket.accept();

                        Logic logic = new Logic(socket);

                        new Thread(logic).start();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } finally {

                       closeClient();

                    }

            }

        });

        looped = true;

        thread.setName( "Client-listener-thread" );
        thread.start();

        connected = true;

        return true;

    }

    public void update(float dt){

        world.step(1/60f, 6, 2);

        orthographicCamera.update();

        if(!iDs.isEmpty()) {

            for (DataOutputStream dataOutputStream : dataOutputStreams) {

                for (int i = 0; i < players.get(iDs.get(dataOutputStream)).bullets.size(); i++) {

                    players.get(iDs.get(dataOutputStream)).bullets.get(i).update(dt);

                    if (players.get(iDs.get(dataOutputStream)).bullets.get(i).shouldRemove()) {

                        players.get(iDs.get(dataOutputStream)).bullets.get(i).getWorld().destroyBody(players.get(iDs.get(dataOutputStream)).bullets.get(i).body);
                        players.get(iDs.get(dataOutputStream)).bullets.remove(i);
                        i--;

                    }

                }

            }

        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, orthographicCamera.combined);

        game.batch.setProjectionMatrix(orthographicCamera.combined);
        game.batch.begin();
        game.batch.end();


    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height);

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

    }

    private class Logic implements Runnable {

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public Logic(Socket socket) {

            this.socket = socket;

        }


        @Override
        public void run() {

            try {

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStreams.add(dataOutputStream);


            } catch (IOException e) {

                e.printStackTrace();

            }

            Runnable runnable = () -> {

                try {

                    synchronized (locker) {

                        for (DataOutputStream dataOutputStream : dataOutputStreams) { //do każdego klienta wyślij:

                            dataOutputStream.writeByte(FrameType.SYNCHRONIZE);

                            for (DataOutputStream j : dataOutputStreams) {

                                dataOutputStream.writeInt(iDs.get(j) );

                                Vector2 position = players.get(iDs.get(j) ).body.getPosition();
                                dataOutputStream.writeFloat(position.x);
                                dataOutputStream.writeFloat(position.y);

                                Vector2 velocity = players.get(iDs.get(j) ).body.getLinearVelocity();
                                dataOutputStream.writeFloat(velocity.x);
                                dataOutputStream.writeFloat(velocity.y);

                            }


                        }

                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            };

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MILLISECONDS);

            while (true) {

                try {

                    interpretFrame();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }

        private boolean interpretFrame() throws IOException {

                int frameType = dataInputStream.readByte();


                switch (frameType) {

                    case FrameType.MOVE:
                        return interpretMove();
                    case FrameType.LOGIN:
                        return interpretLogin();
                    case FrameType.SHOOT:
                        return interpretShoot();
                    default:
                        return false;

                }


        }

        private boolean interpretMove() throws IOException {

            synchronized (locker) {

                players.get(iDs.get(dataOutputStream) ).move(dataInputStream.readByte());

                return true;

            }

        }

        private boolean interpretLogin() throws IOException {

            synchronized (locker) {

                dataOutputStream.writeBoolean(true);
                Random random = new Random();
                int iD = random.nextInt(Integer.MAX_VALUE);
                Thread.currentThread().setName("Client-id-" + iD + "-logic-thread");

                dataOutputStream.writeInt(iD);

                dataOutputStream.writeInt(iDs.size() );


                if(!iDs.isEmpty()) {

                    for (DataOutputStream dataOutputStream : dataOutputStreams) {

                        if (iDs.containsKey(dataOutputStream) ) {

                            this.dataOutputStream.writeInt(iDs.get(dataOutputStream) );

                        }

                    }

                }

                for (DataOutputStream dataOutputStream : dataOutputStreams) {

                    dataOutputStream.writeByte(FrameType.NEW_PLAYER);
                    dataOutputStream.writeInt(iD);

                }

                players.put(iD, new Player(Server.this, iD) );
                iDs.put(dataOutputStream, iD);

                return true;

            }

        }

        private boolean interpretShoot() throws IOException {

            synchronized (locker) {

                players.get(iDs.get(dataOutputStream) ).bullets.add(new Bullet(players.get(iDs.get(dataOutputStream) ), dataInputStream.readFloat() ) );

                return true;

            }

        }

    }

    private void closeClient() {

        //closeObject(dataInputStream);
        //closeObject(dataOutputStream);
        //socket.close();

    }

    private boolean closeObject(Closeable object) {
        if (object == null)
            return false;

        try
        {
            object.close();

            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public World getWorld() {
        return world;
    }

    public OrthographicCamera getOrthographicCamera() {
        return orthographicCamera;
    }

    public Viewport getViewport() {
        return viewport;
    }

}
