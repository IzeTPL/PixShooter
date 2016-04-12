package com.stickshooter.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixServer;
import com.stickshooter.PixShooter;
import com.stickshooter.tools.B2WorldCreator;
import com.stickshooter.tools.DebugOverlay;
import com.stickshooter.tools.WorldContactListener;

import java.io.*;

/**
 * Created by Marian on 08.04.2016.
 */
public class Server extends ScreenAdapter{

    private PixServer game;
    private Object locker = new Object();

    //elementy ekranu
    private Viewport gameViewport;
    private OrthographicCamera gamecam;

    //mapa
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private MapProperties mapProperties;

    //box2D
    private World world;
    private Box2DDebugRenderer b2dr;

    private Player player;

    public Stage stage;
    private Viewport viewport;

    private Label[] labels;
    private final int labelCount = 3;

    //networking
    private ServerSocketHints serverSocketHints;
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    private java.net.Socket javaSocket = null;
    private java.net.ServerSocket javaServerSocket = null;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean connected = false; // informuje o stanie otwartych socket�w logik� zewn�trzn�
    private boolean looped = false; // podtrzymuje wszystkie p�tle
    private Thread thread;


    public Server(PixServer game) {

        this.game = game;

        gamecam = new OrthographicCamera();
        gameViewport = new FitViewport(PixShooter.downScale(PixServer.V_WIDTH/PixServer.SCALE), PixShooter.downScale(PixServer.V_HEIGHT/PixServer.SCALE), gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("test.tmx");
        mapProperties = new MapProperties();
        mapProperties = map.getProperties();
        gamecam.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);

        world = new World(new Vector2(0, -10f), true);
        player = new Player(world, this, gamecam, gameViewport);

        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);


        world.setContactListener(new WorldContactListener());

        create();

        //debug();

    }

    public boolean create() {

        if (connected) return false;

        serverSocketHints = new ServerSocketHints();
        serverSocketHints.acceptTimeout = 1000000;
        //serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, "localhost", 1337, serverSocketHints);
        try
        {
            javaServerSocket = new java.net.ServerSocket( 1337 );
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

                        //socket = serverSocket.accept(null);
                        javaSocket = javaServerSocket.accept();

                        //dataInputStream = new DataInputStream(socket.getInputStream());
                        //dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataInputStream = new DataInputStream(javaSocket.getInputStream());
                        dataOutputStream = new DataOutputStream(javaSocket.getOutputStream());

                        while ( true && interpretFrame() );

                    } catch (IOException e) {

                        e.printStackTrace();

                    } finally {
                       closeClient();
                    }

            }

        });

        looped = true;

        thread.setName( "Client thread" );
        thread.start();

        connected = true;

        return true;

    }

    public void update(float dt){

        world.step(1/60f, 6, 2);

        gamecam.update();

    }

    @Override
    public void render(float delta) {

        //updateDebug();
        update(delta);


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        game.batch.end();

        //game.batch.setProjectionMatrix(stage.getCamera().combined);
        //stage.draw();

    }

    @Override
    public void resize(int width, int height) {

        gameViewport.update(width, height);

    }

    @Override
    public void dispose() {

    }

    private boolean interpretFrame() throws IOException {

        int frameType = dataInputStream.readByte();

        switch(frameType) {

            case FrameType.MOVE:
                return interpretMove();
            case FrameType.LOGIN:
                return true;
            case FrameType.SYNCHRONIZE:
                return interpretSynchronize();
            default:
                return false;

        }

    }

    private boolean interpretMove() throws IOException {

        player.move( dataInputStream.readByte() );

        return true;

    }

    private boolean interpretSynchronize() throws IOException {

        synchronized ( locker )
        {
            if( dataOutputStream != null )
            {

                dataOutputStream.writeByte( FrameType.SYNCHRONIZE );
                Vector2 vector2 = player.body.getLinearVelocity();

                //dataOutputStream.writeFloat(player.body.getPosition().x);
                //dataOutputStream.writeFloat(player.body.getPosition().y);

                dataOutputStream.writeFloat(vector2.x);
                dataOutputStream.writeFloat(vector2.y);

            }

        }

        return true;

    }

    private boolean interpretLoginFrame() throws IOException
    {
        return false;
    }

    private void debug() {

        viewport = new FitViewport(PixShooter.V_WIDTH, PixShooter.V_HEIGHT, new OrthographicCamera());

        labels = new Label[labelCount];

        stage = new Stage(viewport, game.batch);

        Table table = new Table();
        table.bottom().right();
        table.setHeight(300);
        table.setWidth(200);
        table.setPosition(Gdx.graphics.getWidth() - table.getWidth(), 0);



        for(int i = 0; i < labelCount; i++) {

            labels[i] = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            table.add(labels[i]).right();
            table.row();

        }

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.75f);
        pixmap.fill();

        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap) ) ) );
        stage.addActor(table);

    }

    private void updateDebug() {

        float x, y;

        labels[0].setText("fps: " + Gdx.graphics.getFramesPerSecond());

        x = player.body.getPosition().x;
        labels[1].setText("x = " + String.format("%.2f", x));

        y = player.body.getPosition().y;
        labels[2].setText("y = " + String.format("%.2f", y));

    }

    private void closeClient()
    {
        closeObject( dataInputStream );
        closeObject( dataOutputStream );
        socket.dispose();

        player = null;
    }

    private boolean closeObject( Closeable object )
    {
        if ( object == null )
            return false;

        try
        {
            object.close();

            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }

}
