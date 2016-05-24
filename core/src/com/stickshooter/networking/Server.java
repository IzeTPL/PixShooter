package com.stickshooter.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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
    private HashMap<DataOutputStream, Integer> IDs;
    private HashMap<Integer, Player> players;
    private float timer;
    private float item;
    private int port = 1337;


    public Server(AbstractGame game) {

        super(game);
        this.game = (PixServer) game;

        IDs = new HashMap<>();
        players = new HashMap<>();

        create();

    }

    public boolean create() {

        if (connected) return false;

        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch ( IOException e )
        {
            port++;
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

                try {
                    stop();
                } catch (IOException ex) {
                    Gdx.app.exit();
                }

            } finally {

                try {
                    stop();
                } catch (IOException e) {
                    Gdx.app.exit();
                }

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

        if(!IDs.isEmpty() && !dataOutputStreams.isEmpty() && (IDs.size() == dataOutputStreams.size() ) ) {

            for (DataOutputStream dataOutputStream : dataOutputStreams) {

                players.get(IDs.get(dataOutputStream)).update(delta);

                if(players.get(IDs.get(dataOutputStream)).shouldRemove() ) {

                    players.get(IDs.get(dataOutputStream)).getWorld().destroyBody(players.get(IDs.get(dataOutputStream)).body);
                    players.get(IDs.get(dataOutputStream)).reset();

                    new Thread( ( ) -> {

                        try {
                            synchronized (locker) {
                                dataOutputStream.writeByte(FrameType.PLAYER_DIED);
                                dataOutputStream.writeInt(IDs.get(dataOutputStream));
                            }
                        } catch (IOException e) {}

                    });

                }

                if(players.get(IDs.get(dataOutputStream)).shouldRespawn() ) {

                    new Thread( ( ) -> {

                        try {
                            synchronized (locker) {
                                dataOutputStream.writeByte(FrameType.PLAYER_RESPAWN);
                                dataOutputStream.writeInt(IDs.get(dataOutputStream));
                            }
                        } catch (IOException e) {}

                    });

                }

                for (int i = 0; i < players.get(IDs.get(dataOutputStream)).bullets.size(); i++) {

                    players.get(IDs.get(dataOutputStream)).bullets.get(i).update(delta);

                    if (players.get(IDs.get(dataOutputStream)).bullets.get(i).shouldRemove()) {

                        players.get(IDs.get(dataOutputStream)).bullets.get(i).getWorld().destroyBody(players.get(IDs.get(dataOutputStream)).bullets.get(i).body);
                        players.get(IDs.get(dataOutputStream)).bullets.remove(i);
                        i--;

                    }

                }

            }

            int i = 0;

            if(item == dataOutputStreams.size()) item = 0;
            if(timer > 5f && item < dataOutputStreams.size()) {
                item++;
                timer = 0f;
            }

            for (DataOutputStream dataOutputStream:dataOutputStreams) {

                if (i == item && players.get(IDs.get(dataOutputStream)).currentState != Player.State.DEAD) {
                    orthographicCamera.position.x = players.get(IDs.get(dataOutputStream)).body.getPosition().x;
                    orthographicCamera.position.y = players.get(IDs.get(dataOutputStream)).body.getPosition().y;
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

    private class Logic implements Runnable {

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private int ID;
        private boolean connected = false;

        public Logic(Socket socket) {

            this.socket = socket;

        }


        @Override
        public void run() {

            try {

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStreams.add(dataOutputStream);
                this.connected = true;

            } catch (IOException e) {
                this.connected = false;
            }

            if(connected) {
                try {

                    while (true) {

                        interpretFrame();
                        broadcast();

                    }

                } catch (IOException e) {
                } finally {

                    for (DataOutputStream dataOutputStream : dataOutputStreams) {

                        if (dataOutputStream != this.dataOutputStream){
                            try {
                                synchronized (locker) {
                                    dataOutputStream.writeByte(FrameType.PLAYER_LEFT);
                                    dataOutputStream.writeInt(ID);
                                }
                            } catch (IOException e) {
                            }
                        }

                    }

                    world.destroyBody(players.get(IDs.get(dataOutputStream)).body);
                    players.remove(IDs.get(dataOutputStream));

                    IDs.remove(dataOutputStream);
                    dataOutputStreams.remove(dataOutputStream);
                    try {
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        Thread.currentThread().join();
                    } catch (IOException | InterruptedException e) {
                    }

                }

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

        private void broadcast() throws IOException {

            for (DataOutputStream dataOutputStream : dataOutputStreams) {

                synchronized (locker) {
                    dataOutputStream.writeByte(FrameType.SYNCHRONIZE);

                    for (DataOutputStream j : dataOutputStreams) {

                        if(players.get(IDs.get(j)).currentState != Player.State.DEAD) {
                            dataOutputStream.writeInt(IDs.get(j));

                            Vector2 position = players.get(IDs.get(j)).body.getPosition();
                            dataOutputStream.writeFloat(position.x);
                            dataOutputStream.writeFloat(position.y);

                            Vector2 velocity = players.get(IDs.get(j)).body.getLinearVelocity();
                            dataOutputStream.writeFloat(velocity.x);
                            dataOutputStream.writeFloat(velocity.y);
                        }

                    }
                }

            }

        }

        private boolean interpretMove() throws IOException {

            synchronized (locker) {

                players.get(IDs.get(dataOutputStream) ).move(dataInputStream.readByte());

                return true;

            }

        }

        private boolean interpretLogin() throws IOException {

            synchronized (locker) {

                dataOutputStream.writeBoolean(true);
                Random random = new Random();
                ID = random.nextInt(Integer.MAX_VALUE);
                Thread.currentThread().setName("Client-id-" + ID + "-logic-thread");

                dataOutputStream.writeInt(ID);
                dataOutputStream.writeInt(IDs.size() );

                if(!IDs.isEmpty()) {

                    for (DataOutputStream dataOutputStream : dataOutputStreams) {

                        if (IDs.containsKey(dataOutputStream) ) {

                            this.dataOutputStream.writeInt(IDs.get(dataOutputStream) );

                        }

                    }

                }

                for (DataOutputStream dataOutputStream : dataOutputStreams) {

                    dataOutputStream.writeByte(FrameType.NEW_PLAYER);
                    dataOutputStream.writeInt(ID);

                }

                players.put(ID, new Player(Server.this) );
                IDs.put(dataOutputStream, ID);

                return true;

            }

        }

        private boolean interpretShoot() throws IOException {

            synchronized (locker) {

                float degrees = dataInputStream.readFloat();
                players.get(IDs.get(dataOutputStream) ).bullets.add(new Bullet(players.get(IDs.get(dataOutputStream) ), degrees) );

                for (DataOutputStream dataOutputStream : dataOutputStreams) {

                    dataOutputStream.writeByte(FrameType.PLAYER_SHOT);
                    dataOutputStream.writeInt(ID);
                    dataOutputStream.writeFloat(degrees);

                }

            }

            return true;

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
