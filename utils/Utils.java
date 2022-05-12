package utils;

import java.util.regex.Pattern;

public class Utils {
    public static boolean validateEmail(String email) {
        String emailRegexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(emailRegexPattern)
            .matcher(email)
            .matches();
    }
}