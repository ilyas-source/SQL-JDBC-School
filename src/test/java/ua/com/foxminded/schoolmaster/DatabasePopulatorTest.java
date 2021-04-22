package ua.com.foxminded.schoolmaster;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.schoolmaster.dao.GroupDAO;
import ua.com.foxminded.schoolmaster.dao.StudentDAO;

public class DatabasePopulatorTest {

    DatabaseConnector databaseConnector;
    StudentDAO studentDAO;
    GroupDAO groupDAO;
    private IDatabaseTester databaseTester;
    private DatabasePopulator databasePopulator;

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

    @BeforeEach
    void fillTables() throws Exception {
	String jdbcUrl = databaseConnector.getConnection().getMetaData().getURL();
	databaseTester = new JdbcDatabaseTester(org.h2.Driver.class.getName(), jdbcUrl);
	databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	databaseTester.setDataSet(readDataSet());
	databaseTester.onSetup();
    }

    @Test
    void givenScriptFileName_onExecuteSqlScript_thenGet5Groups() throws SQLException, URISyntaxException {
	databasePopulator.executeSqlScript("script.sql");

	int actual = groupDAO.getAll().size();

	assertEquals(5, actual);
    }

    private IDataSet readDataSet() throws Exception {
	ClassLoader classLoader = getClass().getClassLoader();
	String file = classLoader.getResource("testdata.xml").getFile();
	return new FlatXmlDataSetBuilder().build(new FileInputStream(file));
    }
}
