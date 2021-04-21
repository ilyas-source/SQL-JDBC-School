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

    private static final String URL_PROPERTY = "database.url";
    private static final String USER_PROPERTY = "database.user";
    private static final String PASSWORD_PROPERTY = "database.password";

    public DatabaseConnector(String propertiesFileName) throws IOException {
	Properties properties = new Properties();
	try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream(propertiesFileName)) {
	    properties.load(inputStream);
	}
	url = properties.getProperty(URL_PROPERTY);
	user = properties.getProperty(USER_PROPERTY);
	password = properties.getProperty(PASSWORD_PROPERTY);
    }

    public Connection getConnection() throws SQLException {
	return DriverManager.getConnection(url, user, password);
    }
}
