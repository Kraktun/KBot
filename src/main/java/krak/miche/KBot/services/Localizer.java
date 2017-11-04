package krak.miche.KBot.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

//Partly from https://github.com/rubenlagus/TelegramBotsExample/blob/master/src/main/java/org/telegram/services/LocalisationService.java
public class Localizer {

    private static final String STRINGS_FILE_NAME = "strings";
    private static final Utf8ResourceBundle defaultLanguage = new Utf8ResourceBundle(STRINGS_FILE_NAME, Locale.ROOT);
    private static final Utf8ResourceBundle italian = new Utf8ResourceBundle(STRINGS_FILE_NAME, new Locale("it", "IT"));

    /**
     * Get a string in default language (en)
     * @param key key of the resource to fetch
     * @return fetched string or error message otherwise
     */
    public static String getString(String key) {
        String result;
        try {
            result = defaultLanguage.getString(key);
        } catch (MissingResourceException e) {
            result = "String not found";
        }
        return result;
    }

    /**
     * Get a string in default language
     * @param key key of the resource to fetch
     * @return fetched string or error message otherwise
     */
    public static String getString(String key, String language) {
        String result;
        try {
            switch (language.toLowerCase()) {
                case "it":
                    result = italian.getString(key);
                    break;
                default:
                    result = defaultLanguage.getString(key);
                    break;
            }
        } catch (MissingResourceException e) {
            result = getString(key);
        }
        return result;
    }

    private static class Utf8ResourceBundle extends ResourceBundle {

        private static final String BUNDLE_EXTENSION = "properties";
        private static final String CHARSET = "UTF-8";
        private static final ResourceBundle.Control UTF8_CONTROL = new UTF8Control();

        Utf8ResourceBundle(String bundleName, Locale locale) {
            setParent(ResourceBundle.getBundle(bundleName, locale, UTF8_CONTROL));
        }

        @Override
        protected Object handleGetObject(String key) {
            return parent.getObject(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return parent.getKeys();
        }

        //From Ruben Bermudez examples, but also from https://stackoverflow.com/a/4660195
        private static class UTF8Control extends Control {
            public ResourceBundle newBundle
                    (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                    throws IllegalAccessException, InstantiationException, IOException {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, BUNDLE_EXTENSION);
                ResourceBundle bundle = null;
                InputStream stream = null;
                if (reload) {
                    URL url = loader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection = url.openConnection();
                        if (connection != null) {
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }
                if (stream != null) {
                    try {
                        bundle = new PropertyResourceBundle(new InputStreamReader(stream, CHARSET));
                    } finally {
                        stream.close();
                    }
                }
                return bundle;
            }
        }
    }
}

