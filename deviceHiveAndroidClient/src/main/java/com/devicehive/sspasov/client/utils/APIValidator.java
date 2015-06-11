package com.devicehive.sspasov.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toni on 11.06.15.
 */
public class APIValidator {
    private static final String TAG = APIValidator.class.getSimpleName();

    private static Pattern pattern;
    private static Matcher matcher;
    String txt="http://nn8170.pg.devicehive.com/api";
    private static final String API_PATTERN = "((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"]*))";

    /**
     * Validate hex with regular expression
     *
     * @param api hex for validation
     * @return true valid hex, false invalid hex
     */
    public static boolean validate(final String api) {
        pattern = Pattern.compile(API_PATTERN);
        matcher = pattern.matcher(api);
        return matcher.matches();

    }
}
