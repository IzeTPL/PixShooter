package com.stickshooter;

import com.stickshooter.networking.Server;

/**
 * Created by Marian on 10.04.2016.
 */
public class PixServer extends AbstractGame{

    @Override
    public void create () {

        super.create();
        setScreen(new Server(this));

    }

}
