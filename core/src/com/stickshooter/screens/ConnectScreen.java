package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.stickshooter.PixClient;
import com.stickshooter.prototypes.AbstractMenuScreen;
import com.stickshooter.tools.DrawableColor;

/**
 * Created by Marian on 22.05.2016.
 */
public class ConnectScreen extends AbstractMenuScreen {

    private Table table;
    private Table login;

    private Label.LabelStyle mainLabelStyle;
    private Label.LabelStyle labelStyle;

    private TextButton connectButton;
    private TextButton backButton;
    private TextButton.TextButtonStyle textButtonStyle;

    private TextField nicknameField;
    private TextField portField;
    private TextField IPField;

    private TextField.TextFieldStyle textFieldStyle;

    private BitmapFont bitmapFont;

    public ConnectScreen(PixClient game) {

        super(game);
        this.game = game;

        bitmapFont = new BitmapFont();

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = generator.generateFont(PixClient.MENU_FONT, 75);
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.RED;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(PixClient.MENU_FONT, 25);
        mainLabelStyle = new Label.LabelStyle();
        mainLabelStyle.font = generator.generateFont(PixClient.MENU_FONT, 100);

        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = bitmapFont;
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = DrawableColor.getColor(Color.WHITE);

        connectButton = new TextButton("CONNECT", textButtonStyle);
        backButton = new TextButton("BACK", textButtonStyle);

        nicknameField = new TextField("", textFieldStyle);
        IPField = new TextField("", textFieldStyle);
        portField = new TextField("", textFieldStyle);

        table = new Table();
        table.debug();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(new Label("CONNECT", mainLabelStyle));
        table.row().padTop(100);

        login = new Table();
        login.add(new Label("NICKNAME", labelStyle));
        login.row();
        login.add(nicknameField).width(500);
        login.row();
        login.add(new Label("IP ADRESS", labelStyle));
        login.row();
        login.add(IPField).width(500);
        login.row();
        login.add(new Label("PORT", labelStyle));
        login.row();
        login.add(portField).width(500);
        login.row();
        login.add(connectButton);
        login.row();
        login.add(backButton);

        table.add(login);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
        connectButton.addListener(new ClickListener());
        backButton.addListener(new ClickListener());

        IPField.setText("127.0.0.1");
        nicknameField.setText("Player");
        portField.setText("1337");

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        super.render(delta);

        if(connectButton.isPressed() ) {

            game.setScreen(new PlayScreen(game, nicknameField.getText(), IPField.getText(), Integer.parseInt(portField.getText())) );

        }

        if(backButton.isPressed()) {

            game.setScreen(new MainMenuScreen(game) );

        }

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
}
