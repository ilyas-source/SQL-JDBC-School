package ua.com.foxminded.schoolmaster;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
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
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import ua.com.foxminded.schoolmaster.dao.StudentDAO;
import ua.com.foxminded.schoolmaster.domain.Student;

public class StudentDAOTest {

    DatabaseConnector databaseConnector;
    StudentDAO studentDAO;
    private IDatabaseTester databaseTester;

    public StudentDAOTest() throws IOException {
	databaseConnector = new DatabaseConnector("application.properties");
	studentDAO = new StudentDAO(databaseConnector);
    }

    private IDataSet readDataSet() throws Exception {
	ClassLoader classLoader = getClass().getClassLoader();
	String file = classLoader.getResource("data.xml").getFile();
	return new FlatXmlDataSetBuilder().build(new FileInputStream(file));
    }

    @BeforeAll
    public static void createTables() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource("schema.sql");
	File file = new File(url.toURI());
	DatabaseConnector databaseConnector = new DatabaseConnector("application.properties");
	RunScript.execute(databaseConnector.getConnection(), new FileReader(file));
    }

    @BeforeEach
    void init() throws Exception {
	String jdbcUrl = databaseConnector.getConnection().getMetaData().getURL();
	databaseTester = new JdbcDatabaseTester(org.h2.Driver.class.getName(), jdbcUrl);
	databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	databaseTester.setDataSet(readDataSet());
	databaseTester.onSetup();
    }

    @Test
    void givenTables_whenGetAll_thenGetStudentsList() throws SQLException {
	List<Student> expected = new ArrayList<>();
	expected.add(new Student(1001, "Peter", "Scholder", 1001));
	expected.add(new Student(1002, "Edzard", "Kiel", 1002));
	expected.add(new Student(1003, "Silvana", "Schmitz", 1003));

	List<Student> actual = studentDAO.getAll();
	System.out.println(actual);
	assertEquals(expected, actual);
    }

    private Student saveToStudent(String query) throws Exception {
	ITable itable = databaseTester.getConnection().createQueryTable("students", query);
	Student student = new Student(Integer
		.valueOf(itable.getValue(0, "student_id").toString()),
		itable.getValue(0, "first_name").toString(),
		itable.getValue(0, "last_name").toString(),
		null);
	if (itable.getValue(0, "group_id") != null) {
	    student.setGroupId(Integer.valueOf(itable.getValue(0, "group_id").toString()));
	}

	return student;
    }

}
