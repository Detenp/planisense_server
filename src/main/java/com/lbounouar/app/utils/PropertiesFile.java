package com.lbounouar.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFile {
    private final Properties properties = new Properties();
    private static PropertiesFile instance;

    public static PropertiesFile getInstance() throws IOException {
        if (instance == null) {
            instance = new PropertiesFile();
            instance.loadProperties();
        }

        return instance;
    }

    private PropertiesFile() {}

    private void loadProperties() throws IOException {
        String file;

        File hypothetical = new File("./application.properties");
        if (hypothetical.exists() && hypothetical.isFile()) {
            file = hypothetical.getPath();
        } else {
            file = ClassLoader.getSystemResource("application.properties").getPath();
            System.out.println("Using default config file");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        }
    }

    public String getPropertyValue(String key) {
        return this.properties.getProperty(key);
    }
}
