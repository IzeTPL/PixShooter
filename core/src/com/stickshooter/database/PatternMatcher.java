package com.stickshooter.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marian on 24.04.2016.
 */
public class PatternMatcher {

    private Pattern pattern;
    private Matcher matcher;

    private static final String loginPattern = "\\w{4,20}";
    private static final String passwordPattern = "\\A(?=\\S*?[0-9])(?=\\S*?[a-z])(?=\\S*?[A-Z])(?=\\S*?[@#$%^&+=])\\S{8,}\\z";

    public boolean checkLogin(String login) {

        pattern = pattern.compile(loginPattern);
        matcher = pattern.matcher(login);

        return matcher.matches();

    }

    public boolean checkPassword(String password) {

        pattern = pattern.compile(passwordPattern);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public boolean checkConfirmPassword(String password, String confirmPassword) {

        pattern = pattern.compile(password);
        matcher = pattern.matcher(confirmPassword);

        return matcher.matches();
    }

}
