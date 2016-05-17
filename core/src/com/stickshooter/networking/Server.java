package com.stickshooter.networking;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.AbstractGame;
import com.stickshooter.PixServer;
import com.stickshooter.prototypes.AbstractPlayScreen;

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
public class Server extends AbstractPlayScreen{

    private PixServer game;

    //server elements and logic
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private boolean connected = false;
    private boolean looped = false;
    private Thread thread;
    private Object locker = new Object();
    private HashSet<DataOutputStream> dataOutputStreams = new HashSet<>();
    private HashMap<DataOutputStream, Integer> iDs;
    private HashMap<Integer, Player> players;
    private float timer;
    private float item;


    public Server(AbstractGame game) {

        super(game);
        this.game = (PixServer) game;

        iDs = new HashMap<>();
        players = new HashMap<>();

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

                try {
                    while(true) {
                        socket = serverSocket.accept();
                        Logic logic = new Logic(socket);
                        new Thread(logic).start();
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {
                    try {
                        stop();
                    } catch (IOException e) {}

                }

        });

        looped = true;

        thread.setName( "Client-listener-thread" );
        thread.start();

        connected = true;

        return true;

    }

    public void stop() throws IOException{

        for (DataOutputStream dataOutputStream:dataOutputStreams) {
            closeObject(dataOutputStream);
        }

        serverSocket.close();

    }

    @Override
    public void update(float delta){

        super.update(delta);

        if(!iDs.isEmpty() && !dataOutputStreams.isEmpty() && (iDs.size() == dataOutputStreams.size() ) ) {

            for (DataOutputStream dataOutputStream : dataOutputStreams) {

                for (int i = 0; i < players.get(iDs.get(dataOutputStream)).bullets.size(); i++) {

                    players.get(iDs.get(dataOutputStream)).bullets.get(i).update(delta);

                    if (players.get(iDs.get(dataOutputStream)).bullets.get(i).shouldRemove()) {

                        players.get(iDs.get(dataOutputStream)).bullets.get(i).getWorld().destroyBody(players.get(iDs.get(dataOutputStream)).bullets.get(i).body);
                        players.get(iDs.get(dataOutputStream)).bullets.remove(i);
                        i--;

                    }

                }

            }

        }

        if(!iDs.isEmpty() && !dataOutputStreams.isEmpty() && (iDs.size() == dataOutputStreams.size() ) ) {

            int i = 0;

            if(item == dataOutputStreams.size()) item = 0;
            if(timer > 5f && item < dataOutputStreams.size()) {
                item++;
                timer = 0f;
            }

            for (DataOutputStream dataOutputStream:dataOutputStreams) {

                if (i == item) {
                    gamecam.position.x = players.get(iDs.get(dataOutputStream)).body.getPosition().x;
                    gamecam.position.y = players.get(iDs.get(dataOutputStream)).body.getPosition().y;
                }
                i++;

            }

            timer += delta;

        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        super.render(delta);
        update(delta);

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
    }

    private void broadcast() throws IOException {

        for (DataOutputStream dataOutputStream : dataOutputStreams) { //do każdego klienta wyślij:

            synchronized (locker) {
                dataOutputStream.writeByte(FrameType.SYNCHRONIZE);

                for (DataOutputStream j : dataOutputStreams) {

                    dataOutputStream.writeInt(iDs.get(j));

                    Vector2 position = players.get(iDs.get(j)).body.getPosition();
                    dataOutputStream.writeFloat(position.x);
                    dataOutputStream.writeFloat(position.y);

                    Vector2 velocity = players.get(iDs.get(j)).body.getLinearVelocity();
                    dataOutputStream.writeFloat(velocity.x);
                    dataOutputStream.writeFloat(velocity.y);

                }
            }

        }

    }

    private class Logic implements Runnable {

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private int iD;

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


            try {
                while (true) {
                    interpretFrame();
                    broadcast();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                for (DataOutputStream dataOutputStream : dataOutputStreams) {

                    try {
                        synchronized (locker) {
                            dataOutputStream.writeByte(FrameType.PLAYER_LEFT);
                            dataOutputStream.writeInt(iD);
                        }
                    } catch (IOException e) {}

                }

                world.destroyBody(players.get(iDs.get(dataOutputStream)).body);
                players.remove(iDs.get(dataOutputStream));

                iDs.remove(dataOutputStream);
                dataOutputStreams.remove(dataOutputStream);
                try {
                    dataInputStream.close();
                    socket.close();
                    Thread.currentThread().join();
                } catch (IOException | InterruptedException e){}

            }



        }

        private void interpretFrame() throws IOException {

            int frameType;

                frameType = dataInputStream.readByte();

                switch (frameType) {

                    case FrameType.MOVE:
                        interpretMove();
                        break;
                    case FrameType.LOGIN:
                        interpretLogin();
                        break;
                    case FrameType.SHOOT:
                        interpretShoot();
                        break;
                    default:
                        break;

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
                iD = random.nextInt(Integer.MAX_VALUE);
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

                float degrees = dataInputStream.readFloat();
                players.get(iDs.get(dataOutputStream) ).bullets.add(new Bullet(players.get(iDs.get(dataOutputStream) ), degrees) );

                for (DataOutputStream dataOutputStream : dataOutputStreams) {

                    dataOutputStream.writeByte(FrameType.PLAYER_SHOT);
                    dataOutputStream.writeInt(iD);
                    dataOutputStream.writeFloat(degrees);

                }

                return true;

            }

        }

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

}
