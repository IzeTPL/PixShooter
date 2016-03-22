package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.StickShooter;
import com.stickshooter.tools.FTFontGenerator;

/**
 * Created by Marian on 17.03.2016.
 */
public class GalleryScreen implements Screen {

    private StickShooter game;

    private OrthographicCamera gamecam;
    private Viewport gameViewport;

    private Stage stage;
    private Table table;
    private Label.LabelStyle labelStyle;

    //przyciski
    private TextButton backButton;
    private TextButton.TextButtonStyle textButtonStyle;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private ImageButton.ImageButtonStyle leftStyle;
    private ImageButton.ImageButtonStyle rightStyle;
    private Image img;

    private FileHandle[] files;
    private Texture tex;

    //generator Bitmap font
    private FTFontGenerator generator;
    private int index = 0;


    public GalleryScreen(StickShooter game) {

        files = Gdx.files.external("Screenshots/").list();

        game.manager.load("arrow1.png", Texture.class);
        game.manager.load("arrow2.png", Texture.class);
        game.manager.load("arrow3.png", Texture.class);
        game.manager.load("arrow4.png", Texture.class);
        game.manager.finishLoading();
        if(files.length < 1) {
            tex = new Texture(Gdx.files.internal("badlogic.jpg"));
        } else {
            tex = new Texture(files[0]);
        }
        Texture right = game.manager.get("arrow1.png", Texture.class);
        Texture left = game.manager.get("arrow2.png", Texture.class);
        Texture rightOver = game.manager.get("arrow3.png", Texture.class);
        Texture leftOver = game.manager.get("arrow4.png", Texture.class);

        this.game = game;

        //kamera
        gamecam = new OrthographicCamera();
        gameViewport = new FitViewport(1280, 720, gamecam);
        gamecam.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);

        //generowanie fontu bitmap

        generator = new FTFontGenerator(game.manager);
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = generator.generateFont(StickShooter.MENU_FONT, 75);
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.RED;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(StickShooter.MENU_FONT, 100);

        leftStyle = new ImageButton.ImageButtonStyle();
        leftStyle.imageUp = new TextureRegionDrawable(new TextureRegion(left));
        leftStyle.imageOver = new TextureRegionDrawable(new TextureRegion(leftOver));

        rightStyle = new ImageButton.ImageButtonStyle();
        rightStyle.imageUp = new TextureRegionDrawable(new TextureRegion(right));
        rightStyle.imageOver = new TextureRegionDrawable(new TextureRegion(rightOver));


        //tworzenie przycisków
        backButton = new TextButton("BACK", textButtonStyle);

        //Strzałki
        img = new Image(tex);
        leftButton = new ImageButton(leftStyle);
        leftButton.addAction(Actions.alpha(0));

        rightButton = new ImageButton(rightStyle);


        img.setScaling(Scaling.fit);


        //układam elementy w kontenerze

        table = new Table();
        table.setClip(true);
        table.debug();
        table.bottom();
        table.setFillParent(true);
        table.add();
        table.add(new Label("Gallery", labelStyle));
        table.add();
        table.row();
        table.add(leftButton);
        table.add(img).expand();
        table.add(rightButton);
        table.row();
        table.add();
        table.add(backButton);
        table.add();



        //dodajemy elementy na scenę
        stage = new Stage(gameViewport, game.batch);
        stage.addActor(table);


        //aktywuje nasłuchiwanie przycisków
        Gdx.input.setInputProcessor(stage);
        backButton.addListener(new ClickListener());
        leftButton.addListener(new ClickListener());
        rightButton.addListener(new ClickListener());

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

        if(backButton.isPressed()) {

            game.setScreen(new MainMenuScreen(game));
            dispose();


        }

        if(files.length > 0) {

            if (((Gdx.input.justTouched() && leftButton.isPressed()) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) && index > 0) {

                --index;
                tex = new Texture(files[index]);
                img.setDrawable(new TextureRegionDrawable(new TextureRegion(tex)));

            }

            if (((Gdx.input.justTouched() && rightButton.isPressed()) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) && index < files.length - 1) {

                ++index;
                tex = new Texture(files[index]);
                img.setDrawable(new TextureRegionDrawable(new TextureRegion(tex)));

            }

        }

        hideArrows();


    }

    private void hideArrows() {

        if(index > 0) {

            leftButton.addAction(Actions.alpha(1, 0.3f));

        } else {

            leftButton.addAction(Actions.alpha(0, 0.3f));

        }

        if(index < files.length - 1) {

            rightButton.addAction(Actions.alpha(1, 0.3f));

        } else {

            rightButton.addAction(Actions.alpha(0, 0.3f));

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
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        stage.dispose();
    }

}
