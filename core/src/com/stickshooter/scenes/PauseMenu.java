package com.stickshooter.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixShooter;
import com.stickshooter.tools.FTFontGenerator;

/**
 * Created by Marian on 13.03.2016.
 */
public class PauseMenu implements Disposable{

    /*TODO:
    * -obsługa klawiatury
    * -obsługa myszki
    * -podswietlenie zaznaczenia
    * */

    private boolean visible = false;

    public Stage stage;
    private Viewport viewport;
    private AssetManager manager;

    //kontener
    private Table table;
    private Table transparency;
    private Label.LabelStyle labelStyle;
    private Pixmap pixmap;


    //przyciski
    private TextButton playButton;
    private TextButton optionsButton;
    private TextButton mainMenuButton;
    private TextButton.TextButtonStyle textButtonStyle;

    //generator Bitmap font
    private FTFontGenerator generator;

    public PauseMenu(SpriteBatch sb, AssetManager manager) {
        this.manager = manager;

        //1 czarny piksel
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();


        viewport = new FitViewport(PixShooter.V_WIDTH, PixShooter.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        //generowanie fontu
        generator = new FTFontGenerator(manager);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = generator.generateFont(PixShooter.MENU_FONT, 75);
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.RED;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(PixShooter.MENU_FONT, 100);

        //tworzenie przycisków
        playButton = new TextButton("RESUME", textButtonStyle);
        optionsButton = new TextButton("OPTIONS", textButtonStyle);
        mainMenuButton = new TextButton("MAIN MENU", textButtonStyle);

        //układanie w kontenerze
        table = new Table();
        table.debug();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(new Label("PAUSE MENU", labelStyle));
        table.row().padTop(100);
            Table subTable = new Table();
            subTable.add(playButton);
            subTable.row();
            subTable.add(optionsButton);
            subTable.row();
            subTable.add(mainMenuButton);
        table.add(subTable);
        table.setVisible(false);

        transparency = new Table();
        transparency.setFillParent(true);
        transparency.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        transparency.addAction(Actions.alpha(0.75f));
        transparency.setVisible(false);

        //nasłuchiwanie akcji
        Gdx.input.setInputProcessor(stage);
        playButton.addListener(new ClickListener());

        stage.addActor(transparency);
        stage.addActor(table);

    }

    @Override
    public void dispose() {

        stage.dispose();

    }

    public TextButton getPlayButton() {
        return playButton;
    }

    public boolean isVisible() {
        return visible;
    }

    public void show() {

        if (!visible) {
            table.setVisible(true);
            transparency.setVisible(true);
            visible = true;
        }

    }

    public void hide() {

        if (visible) {
            table.setVisible(false);
            transparency.setVisible(false);
            visible = false;
        }
    }

    public TextButton getMainMenuButton() {
        return mainMenuButton;
    }



}
