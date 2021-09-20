package dao;

import config.DAOConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Connection holder class
public class ConnectionClass {
    private static ConnectionClass INSTANCE;
    private static Connection connection;

    private ConnectionClass() {
        connection = raiseConnection();
    }

    public static ConnectionClass getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionClass();
        }
        return INSTANCE;
    }

    private Connection raiseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DAOConfig.INSTANCE.getDbUrl(), DAOConfig.INSTANCE.getDbUser(), DAOConfig.INSTANCE.getDbPassword());
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
