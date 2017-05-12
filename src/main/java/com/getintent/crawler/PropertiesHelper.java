package com.getintent.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by arkadiy on 12/05/17.
 */
public class PropertiesHelper {

    public static String getString(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing key '" + key + "' in application properties");
        }

        return value;
    }

    public static int getInt(Properties properties, String key) {
        String stringValue = getString(properties, key);
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException x) {
            throw new IllegalArgumentException("Property '" + key + "' is not integer", x);
        }
    }

    public static int getPositiveInt(Properties properties, String key) {
        int val = getInt(properties, key);

        if (val > 0) {
            return val;
        } else {
            throw new IllegalArgumentException("Property '" + key + "' is not positive int");
        }
    }

    public static int getNonNegativeInt(Properties properties, String key) {
        int val = getInt(properties, key);
        if (val >= 0) {
            return val;
        } else {

        } throw new IllegalArgumentException("Property '" + key + "' is not non-negative int");
    }

    public static String getUrl(Properties properties, String key) {
        String urlString = getString(properties, key);

        try {
            new URL(urlString);
            return urlString;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Property '" + key + "' is not url");
        }
    }
}
