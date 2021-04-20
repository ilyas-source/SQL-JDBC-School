package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private String url;
    private String user;
    private String password;

    public DatabaseConnector() throws IOException {
	Properties properties = new Properties();
	try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream("application.properties")) {
	    properties.load(inputStream);
	}
	this.url = properties.getProperty("database.url");
	this.user = properties.getProperty("database.user");
	this.password = properties.getProperty("database.password");
    }

    public Connection getConnection() throws SQLException {
	return DriverManager.getConnection(url, user, password);
    }

    @Override
    public String toString() {
	return this.url + ", " + this.user + ":" + this.password;
    }
}
