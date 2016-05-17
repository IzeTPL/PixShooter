package com.stickshooter;

import com.stickshooter.screens.PlayScreen;


public class PixClient extends AbstractGame{

    @Override
    public void create () {

        super.create();
        setScreen(new PlayScreen(this));
        //setScreen(new LoginScreen(this));

    }

}

