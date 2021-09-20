package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Configuration class that loads the properties to get access to database
public enum DAOConfig {
    INSTANCE;

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    DAOConfig(){
        loadProperties();
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    private void loadProperties(){
        try(InputStream in = getClass().getClassLoader().getResourceAsStream("dbProperties.properties")){
            Properties properties = new Properties();
            properties.load(in);
            dbUrl = properties.getProperty("dbUrl");
            dbUser = properties.getProperty("dbUser");
            dbPassword = properties.getProperty("dbPassword");
        } catch (IOException ignored){ }
    }
}
