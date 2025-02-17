package core.clients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiClient {

    private final String baseUrl;

    public ApiClient() {
        this.baseUrl = determineBaseUrl();
    }

    private String determineBaseUrl() {
       String environment =  System.getProperty("env","test");
        String configFileName = "application" + environment + ".properties";

        Properties properties =  new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalAccessException("Configuration file found: " + configFileName);

            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalAccessException(("Unable to load configuration file:" + configFileName, e))
            }
        }
    }

