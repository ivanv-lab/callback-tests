package ru.git.ivanv_lab.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyProvider {

    private static final Properties properties = new Properties();
    private static final String filename = "tests.properties";

    static {
        try (InputStream is = ClassLoader.getSystemResourceAsStream(filename)) {
            if (is == null) {
                throw new RuntimeException("Не удалось найти файл" + filename);
            }

            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String apiUrl = properties.getProperty("api.url");
    private static final String uiUrl = properties.getProperty("ui.url");
    private static final String dbUrl = properties.getProperty("db.url");

    public static String getApiUrl() {
        return apiUrl;
    }

    public static String getUiUrl() {
        return uiUrl;
    }

    public static String getDbUrl() {
        return dbUrl;
    }
}
