package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton che gestisce la connessione al database PostgreSQL.
 */
public class DBManager {
    private static DBManager instance;
    private final String url;
    private final String user;
    private final String password;

    private DBManager() {
        Properties props = new Properties();
        try (InputStream input = DBManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Impossibile trovare db.properties");
            }
            props.load(input);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Errore nella lettura di db.properties", e);
        }
    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

