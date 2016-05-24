package com.stickshooter;

import com.stickshooter.screens.LoginScreen;


public class PixClient extends AbstractGame{

    public String login;
    public String password;

    @Override
    public void create () {

        super.create();
        setScreen(new LoginScreen(this));

    }

}

