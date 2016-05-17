package com.stickshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stickshooter.PixClient;
import com.stickshooter.database.DatabaseClient;
import com.stickshooter.database.PatternMatcher;
import com.stickshooter.tools.DrawableColor;
import com.stickshooter.tools.FTFontGenerator;

/**
 * Created by Marian on 25.04.2016.
 */
public class LoginScreen implements Screen {

    private PixClient game;
    private DatabaseClient databaseClient;

    private OrthographicCamera gamecam;
    private Viewport gameViewport;

    private Stage stage;
    private Table table;
    private Table createUser;
    private Table login;

    private Label loginLabel;
    private Label newUserLabel;
    private Label loginResultLabel;
    private Label createResultLabel;
    private Label.LabelStyle labelStyle;
    private Label.LabelStyle resultStyle;

    private TextButton loginButton;
    private TextButton createUserButton;
    private TextButton newUserButton;
    private TextButton backButton;
    private TextButton.TextButtonStyle textButtonStyle;

    private TextField loginField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private TextField newUserField;
    private TextField newPasswordField;
    private TextField.TextFieldStyle defaultFieldStyle;
    private TextField.TextFieldStyle redFieldStyle;
    private TextField.TextFieldStyle greenFieldStyle;
    private BitmapFont standardBitmapFont;


    //generator Bitmap font
    private FTFontGenerator generator;
    private PatternMatcher patternMatcher;



    public LoginScreen(PixClient game) {

        this.game = game;
        patternMatcher = new PatternMatcher();
        standardBitmapFont = new BitmapFont();

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

        defaultFieldStyle = new TextField.TextFieldStyle();
        defaultFieldStyle.font = standardBitmapFont;
        defaultFieldStyle.fontColor = Color.BLACK;
        defaultFieldStyle.background = DrawableColor.getColor(Color.WHITE);

        redFieldStyle = new TextField.TextFieldStyle();
        redFieldStyle.font = standardBitmapFont;
        redFieldStyle.fontColor = Color.BLACK;
        redFieldStyle.background = DrawableColor.getColor(Color.RED);

        greenFieldStyle = new TextField.TextFieldStyle();
        greenFieldStyle.font = standardBitmapFont;
        greenFieldStyle.fontColor = Color.BLACK;
        greenFieldStyle.background = DrawableColor.getColor(Color.GREEN);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(PixClient.MENU_FONT, 50);
        labelStyle.fontColor = Color.WHITE;

        resultStyle = new Label.LabelStyle();
        resultStyle.font = standardBitmapFont;
        resultStyle.fontColor = Color.WHITE;

        loginField = new TextField("", defaultFieldStyle);
        passwordField = new TextField("", defaultFieldStyle);
        confirmPasswordField = new TextField("", defaultFieldStyle);
        newPasswordField = new TextField("", defaultFieldStyle);
        newUserField = new TextField("", defaultFieldStyle);

        loginButton = new TextButton("LOGIN", textButtonStyle);
        createUserButton = new TextButton("CREATE", textButtonStyle);
        backButton = new TextButton("BACK", textButtonStyle);
        newUserButton = new TextButton("CREATE USER", textButtonStyle);

        newUserLabel = new Label("NEW USER", labelStyle);
        loginLabel = new Label("LOGIN", labelStyle);
        loginResultLabel = new Label("", resultStyle);
        createResultLabel = new Label("", resultStyle);

        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');

        //układam elementy w kontenerze
        table = new Table();
        table.debug();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(loginLabel);
        table.row().padTop(100);

        login = new Table();
        login.add(new Label("USERNAME", labelStyle));
        login.row();
        login.add(loginField).width(500);
        login.row();
        login.add(new Label("PASSWORD", labelStyle));
        login.row();
        login.add(passwordField).width(500);
        login.row();
        login.add(loginButton);
        login.row();
        login.add(newUserButton);
        login.row();
        login.add(loginResultLabel);

        createUser = new Table();
        createUser.add(new Label("USERNAME", labelStyle));
        createUser.row();
        createUser.add(newUserField).width(500);
        createUser.row();
        createUser.add(new Label("PASSWORD", labelStyle));
        createUser.row();
        createUser.add(newPasswordField).width(500);
        createUser.row();
        createUser.add(new Label("CONFIRM PASSWORD", labelStyle));
        createUser.row();
        createUser.add(confirmPasswordField).width(500);
        createUser.row();
        createUser.add(createUserButton);
        createUser.row();
        createUser.add(backButton);
        createUser.row();
        createUser.add(createResultLabel);

        table.add(login);

        //dodajemy elementy na scenę
        stage = new Stage(gameViewport, game.batch);
        stage.addActor(table);

        //aktywuje nasłuchiwanie przycisków
        Gdx.input.setInputProcessor(stage);
        loginButton.addListener(new ClickListener());
        createUserButton.addListener(new ClickListener());
        newUserButton.addListener(new ClickListener());
        backButton.addListener(new ClickListener());



        databaseClient = new DatabaseClient();
        databaseClient.connect();

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

        updateFields();
        handleInput();

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

    public void updateFields() {

        if (!newUserField.getText().isEmpty()) {
            if (patternMatcher.checkLogin(newUserField.getText())) {

                newUserField.setStyle(greenFieldStyle);

            } else {

                newUserField.setStyle(redFieldStyle);

            }
        } else {

            newUserField.setStyle(defaultFieldStyle);

        }

        if (!newPasswordField.getText().isEmpty()) {
            if (patternMatcher.checkPassword(newPasswordField.getText())) {

                newPasswordField.setStyle(greenFieldStyle);

            } else {

                newPasswordField.setStyle(redFieldStyle);

            }
        } else {

            newPasswordField.setStyle(defaultFieldStyle);

        }

        if (!confirmPasswordField.getText().isEmpty()) {
            if (patternMatcher.checkConfirmPassword(newPasswordField.getText(), confirmPasswordField.getText())) {

                confirmPasswordField.setStyle(greenFieldStyle);

            } else {

                confirmPasswordField.setStyle(redFieldStyle);

            }
        } else {

            confirmPasswordField.setStyle(defaultFieldStyle);

        }

    }

    public void handleInput() {

        if(Gdx.input.justTouched() && loginButton.isPressed()) {

            if(databaseClient.verifyUser(loginField.getText(), passwordField.getText() ) ) {

                resultStyle.fontColor = Color.GREEN;
                loginResultLabel.setText("Successfully logged in");

            } else {

                resultStyle.fontColor = Color.RED;
                loginResultLabel.setText("Something went wrong");

            }

        }

        if(Gdx.input.justTouched() && createUserButton.isPressed() && patternMatcher.checkConfirmPassword(newPasswordField.getText(), confirmPasswordField.getText() ) ) {

            if(databaseClient.createUser(newUserField.getText(), newPasswordField.getText() ) ) {

                resultStyle.fontColor = Color.GREEN;
                createResultLabel.setText("Successfully created user");

            } else {

                resultStyle.fontColor = Color.RED;
                createResultLabel.setText("Something went wrong");

            }


        }

        if(Gdx.input.justTouched() && newUserButton.isPressed() ) {

            changeTable(newUserLabel);
            table.add(createUser);

        }

        if(Gdx.input.justTouched() && backButton.isPressed() ) {

            changeTable(loginLabel);
            table.add(login);

        }

    }

    public void changeTable(Label label) {

        table.clear();
        table.top().padTop(50);
        table.setFillParent(true);
        table.add(label);
        table.row().padTop(100);

    }

}
