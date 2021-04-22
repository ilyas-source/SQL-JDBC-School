package ua.com.foxminded.schoolmaster.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.schoolmaster.DatabaseConnector;
import ua.com.foxminded.schoolmaster.domain.Group;

class GroupDAOTest {

    DatabaseConnector databaseConnector;
    GroupDAO groupDAO;
    private IDatabaseTester databaseTester;

    public GroupDAOTest() throws IOException, SQLException {
	this.databaseConnector = new DatabaseConnector("application.properties");
	this.groupDAO = new GroupDAO(databaseConnector);
    }

    @BeforeAll
    public static void createSchema() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource("schema.sql");
	File file = new File(url.toURI());
	DatabaseConnector DatabaseConnector = new DatabaseConnector("application.properties");
	RunScript.execute(DatabaseConnector.getConnection(), new FileReader(file));
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
    void givenNewDatabase_whenGetAll_then3Groups() throws SQLException {
	List<Group> expected = new ArrayList<>();
	expected.add(new Group(1, "QW-01"));
	expected.add(new Group(2, "AS-02"));
	expected.add(new Group(3, "ZX-03"));
	expected.add(new Group(4, "ER-04"));

	assertEquals(expected, groupDAO.getAll());
    }

    @Test
    void givenNewGroup_whenCreate_thenNewGroup() throws Exception {
	Group expected = new Group(1, "Foo");
	groupDAO.create(expected);
	Group actual = mapToGroup("select group_id, group_name from groups where (group_name = 'Foo');");
	expected.setId(actual.getId());

	assertEquals(expected, actual);
    }

    @Test
    void givenEmptyName_whenCreate_thenError() throws SQLException {
	assertThrows(SQLException.class, () -> {
	    groupDAO.create(new Group(1, null));
	});
    }

    @Test
    void givenStudentsCount1_whenGetByStudentsCount_then3Groups() throws SQLException {
	List<Group> expected = new ArrayList<>();
	expected.add(new Group(1, "QW-01"));
	expected.add(new Group(2, "AS-02"));
	expected.add(new Group(3, "ZX-03"));

	assertEquals(expected, groupDAO.getByLessThanCount(1));
    }

    private Group mapToGroup(String query) throws Exception {
	ITable itable = databaseTester.getConnection().createQueryTable("groups", query);
	return new Group(Integer.valueOf(itable.getValue(0, "group_id").toString()),
		itable.getValue(0, "group_name").toString());
    }

    private IDataSet readDataSet() throws Exception {
	ClassLoader classLoader = getClass().getClassLoader();
	String file = classLoader.getResource("testdata.xml").getFile();
	return new FlatXmlDataSetBuilder().build(new FileInputStream(file));
    }

}
