package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.stickshooter.PixClient;
import com.stickshooter.prototypes.AbstractMenuScreen;

/**
 * Created by Marian on 12.03.2016.
 */
public class MainMenuScreen extends AbstractMenuScreen {

    private Table table;
    private Table sign;

    private Label.LabelStyle labelStyle;
    private Label.LabelStyle authorStyle;

    private TextButton playButton;
    private TextButton galleryButton;
    private TextButton exitButton;
    private TextButton.TextButtonStyle textButtonStyle;

    public MainMenuScreen(PixClient game) {

        super(game);
        this.game = game;

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = generator.generateFont(PixClient.MENU_FONT, 75);
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.RED;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(PixClient.MENU_FONT, 100);

        authorStyle = new Label.LabelStyle();
        authorStyle.font = generator.generateFont(PixClient.SIGN_FONT, 45);

        playButton = new TextButton("PLAY", textButtonStyle);
        galleryButton = new TextButton("GALLERY", textButtonStyle);
        exitButton = new TextButton("EXIT", textButtonStyle);

        table = new Table();
        table.debug();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(new Label("MAIN MENU", labelStyle));
        table.row().padTop(100);
            Table subTable = new Table();
            subTable.add(playButton);
            subTable.row();
            subTable.add(galleryButton);
            subTable.row();
            subTable.add(exitButton);
        table.add(subTable);

        sign = new Table();
        sign.debug();
        sign.bottom().right().padBottom(50).padRight(50);
        sign.setFillParent(true);
        sign.add(new Label("by: Marcin Slupek", authorStyle));

        stage.addActor(table);
        stage.addActor(sign);

        //aktywuje nasłuchiwanie przycisków
        Gdx.input.setInputProcessor(stage);
        playButton.addListener(new ClickListener());
        exitButton.addListener(new ClickListener());

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        super.render(delta);

        if(playButton.isPressed() ) {

            game.setScreen(new ConnectScreen(game) );

        }

        if(galleryButton.isPressed()) {

            game.setScreen(new GalleryScreen(game) );

        }

        if(exitButton.isPressed()) {

            Gdx.app.exit();

        }

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width,height);
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
}
