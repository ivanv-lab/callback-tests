package ru.git.ivanv_lab.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyProvider {

    private static final Properties properties=new Properties();
    private static final String filename="tests.properties";

    static {
        try(InputStream is=ClassLoader.getSystemResourceAsStream(filename)){
            if(is==null){
                throw new RuntimeException("Не удалось найти файл" + filename);
            }

            properties.load(is);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static final String baseUrl= properties.getProperty("base.url");

    public static String getBaseUrl(){
        return baseUrl;
    }
}
