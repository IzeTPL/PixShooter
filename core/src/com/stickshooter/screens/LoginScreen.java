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
import com.stickshooter.database.DatabaseClient;
import com.stickshooter.database.PatternMatcher;
import com.stickshooter.prototypes.AbstractMenuScreen;
import com.stickshooter.tools.DrawableColor;

/**
 * Created by Marian on 25.04.2016.
 */
public class LoginScreen extends AbstractMenuScreen {

    private DatabaseClient databaseClient;

    private Table table;
    private Table createUser;
    private Table login;

    private Label loginLabel;
    private Label newUserLabel;
    private Label loginResultLabel;
    private Label createResultLabel;
    private Label.LabelStyle resultStyle;
    private Label.LabelStyle mainLabelStyle;
    private Label.LabelStyle labelStyle;

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

    private PatternMatcher patternMatcher;

    public LoginScreen(PixClient game) {

        super(game);
        this.game = game;
        patternMatcher = new PatternMatcher();
        standardBitmapFont = new BitmapFont();

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
        labelStyle.font = generator.generateFont(PixClient.MENU_FONT, 25);
        mainLabelStyle = new Label.LabelStyle();
        mainLabelStyle.font = generator.generateFont(PixClient.MENU_FONT, 100);

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

        newUserLabel = new Label("NEW USER", mainLabelStyle);
        loginLabel = new Label("SIGN IN", mainLabelStyle);
        loginResultLabel = new Label("", resultStyle);
        createResultLabel = new Label("", resultStyle);

        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');

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

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
        loginButton.addListener(new ClickListener());
        createUserButton.addListener(new ClickListener());
        newUserButton.addListener(new ClickListener());
        backButton.addListener(new ClickListener());

        databaseClient = new DatabaseClient();
        databaseClient.connect();

        loginField.setText("test");
        passwordField.setText("test@123A");

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        super.render(delta);

        updateFields();
        handleInput();

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
                game.login = loginField.getText();
                game.password = passwordField.getText();
                game.setScreen(new MainMenuScreen(game) );

            } else {

                resultStyle.fontColor = Color.RED;
                loginResultLabel.setText("Incorrect username or password");

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
