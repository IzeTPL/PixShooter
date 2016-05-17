package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixClient;
import com.stickshooter.tools.FTFontGenerator;

/**
 * Created by Marian on 10.03.2016.
 */
public class OptionsScreen implements Screen {

    /* TODO zaimplementowac:
     * -zmiana rodzielczosci
     * -suwaki glosnosci muzyki i efektow
     * -fullscreen on/off
     * -borderless fullscreen
     * -obsługa klawiatury
     * -obsługa myszki
     * -podswietlenie zaznaczenia*/

    private PixClient game;

    private OrthographicCamera gamecam;
    private Viewport gameViewport;

    private Stage stage;
    private Table table;
    private Table sign;
    private Label.LabelStyle labelStyle;
    private Label.LabelStyle authorStyle;

    //przyciski
    private TextButton playButton;
    private TextButton optionsButton;
    private TextButton galleryButton;
    private TextButton exitButton;
    private TextButton.TextButtonStyle textButtonStyle;

    //generator Bitmap font
    private FTFontGenerator generator;



    public OptionsScreen(PixClient game) {

        this.game = game;

        //kamera
        gamecam = new OrthographicCamera();
        gameViewport = new FitViewport(PixClient.V_WIDTH, PixClient.V_HEIGHT, gamecam);
        gamecam.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);

        //generowanie fontu bitmap
        generator = new FTFontGenerator(game.manager);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = generator.generateFont(PixClient.MENU_FONT, 75);
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.RED;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(PixClient.MENU_FONT, 100);

        authorStyle = new Label.LabelStyle();
        authorStyle.font = generator.generateFont(PixClient.SIGN_FONT, 45);

        //tworzenie przycisków
        playButton = new TextButton("PLAY", textButtonStyle);
        optionsButton = new TextButton("OPTIONS", textButtonStyle);
        galleryButton = new TextButton("GALLERY", textButtonStyle);
        exitButton = new TextButton("EXIT", textButtonStyle);

        //układam elementy w kontenerze
        table = new Table();
        table.debug();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(new Label("OPTIONS", labelStyle));
        table.row().padTop(100);
        Table subTable = new Table();
        subTable.add(playButton);
        subTable.row();
        subTable.add(optionsButton);
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


        //dodajemy elementy na scenę
        stage = new Stage(gameViewport, game.batch);
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


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act();

        if(playButton.isPressed()) {

            game.setScreen(new PlayScreen(game));
            dispose();

        }

        if(galleryButton.isPressed()) {

            game.setScreen(new GalleryScreen(game));
            dispose();

        }

        if(exitButton.isPressed()) {

            Gdx.app.exit();

        }

    }

    @Override
    public void resize(int width, int height) {

        gameViewport.update(width, height);

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
        generator.dispose();
        stage.dispose();
    }
}
