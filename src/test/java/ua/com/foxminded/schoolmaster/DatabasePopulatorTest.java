package ua.com.foxminded.schoolmaster;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.dbunit.IDatabaseTester;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;

import ua.com.foxminded.schoolmaster.dao.StudentDAO;

public class DatabasePopulatorTest {

    DatabaseConnector databaseConnector;
    StudentDAO studentDAO;
    private IDatabaseTester databaseTester;

    public DatabasePopulatorTest() throws IOException {
	databaseConnector = new DatabaseConnector("application.properties");
	DatabasePopulator databasePopulator = new DatabasePopulator(databaseConnector);
    }

    @BeforeAll
    public static void createTables() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource("schema.sql");
	File file = new File(url.toURI());
	DatabaseConnector databaseConnector = new DatabaseConnector("application.properties");
	RunScript.execute(databaseConnector.getConnection(), new FileReader(file));
    }

}
