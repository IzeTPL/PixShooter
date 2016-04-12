package com.stickshooter.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.physics.box2d.Body;
import com.stickshooter.sprites.*;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marian on 10.04.2016.
 */
public class Client {

    SocketHints hints;
    Socket socket;
    public float x = 1f, y = 1f;

    private DataInputStream dataInputStream;

    private DataOutputStream dataOutputStream;

    private java.net.Socket javaSocket = null;

    private boolean connected = false;
    private boolean looped = false;

    private int userId;
    private String userLogin;
    private Thread thread = null;
    private Thread synchronization = null;

    public Client() {

        hints = new SocketHints();

    }

    public boolean connect(String login) {

        if ( this.connected )
            return false;

        try {

            //socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "localhost", 1337, hints);
            javaSocket = new java.net.Socket("localhost", 1337);

           // dataInputStream = new DataInputStream(socket.getInputStream());
            //dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataInputStream = new DataInputStream(javaSocket.getInputStream());
            dataOutputStream = new DataOutputStream(javaSocket.getOutputStream());

            dataOutputStream.writeByte( FrameType.LOGIN );
            //dataOutputStream.writeUTF( login );

            runThread();

            //if ( dataInputStream.readBoolean() ) // sprawdzenie wyniku logowania
            //{
            //    // pobieranie danych zalogowanego u�ytkownika
            //    //userId = dataInputStream.readInt();
            //    //userLogin = login;
//
            //    //short userX = dataInputStream.readShort();
            //    //short userY = dataInputStream.readShort();
//
            //    connected = true;
            //    looped = true;
//
            //    //this.event.userLogged( this.userId, login, userX, userY );
//
            //    runThread();
//
            //    return true;
            //} else closeObjects();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return false;

    }

    private void runThread() {

        thread = new Thread(() ->
        {
            boolean error = false;

            try
            {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dataOutputStream.writeByte(FrameType.SYNCHRONIZE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
                executorService.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.MILLISECONDS);

                while ( looped || true ) {

                    interpretFrame();

                }
            }
            catch ( Exception e )
            {
                error = true;
            }
            finally
            {
                closeObjects();

                looped = false;
                connected = false;

                if ( error );
                    //this.event.connectionError();
            }
        });

        thread.setName( "Client thread" );
        thread.start();
    }

    private void interpretFrame() throws Exception {

        int frameType = dataInputStream.readByte();

        switch(frameType) {

            case FrameType.MOVE:
                interpretMove();
                break;
            case FrameType.SYNCHRONIZE:
                interpretSynchronize();
            default:
                break;

        }

    }

    private void interpretMove() throws Exception{

        x = dataInputStream.readFloat();
        y = dataInputStream.readFloat();

    }

    private void interpretSynchronize() throws Exception{

        x = dataInputStream.readFloat();
        y = dataInputStream.readFloat();

    }

    public void jump() throws IOException{

        dataOutputStream.writeByte(FrameType.MOVE);
        dataOutputStream.writeByte(MovementType.JUMP);

    }

    public void moveRight() throws IOException{

        dataOutputStream.writeByte(FrameType.MOVE);
        dataOutputStream.writeByte(MovementType.RIGHT);

    }

    public void moveLeft() throws IOException{

        dataOutputStream.writeByte(FrameType.MOVE);
        dataOutputStream.writeByte(MovementType.LEFT);

    }

    private synchronized void closeObjects()
    {
        closeObject( dataOutputStream );
        closeObject( dataInputStream );
        socket.dispose();
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
