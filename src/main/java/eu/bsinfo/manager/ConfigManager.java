package eu.bsinfo.manager;

import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String fileName = "config.properties";
    private static Properties config;

    private static Properties getConfig() throws IOException {
        if (config == null) {
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
            config = props;
        }
        return config;
    }

    public static String getProperty(ConfigProperties key) throws IOException {
        Properties config = getConfig();
        return config.getProperty(String.valueOf(key));
    }
}
