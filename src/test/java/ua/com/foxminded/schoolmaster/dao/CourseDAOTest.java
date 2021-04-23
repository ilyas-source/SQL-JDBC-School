package ua.com.foxminded.schoolmaster.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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

import ua.com.foxminded.schoolmaster.ConnectionProvider;
import ua.com.foxminded.schoolmaster.domain.Course;

class CourseDAOTest {

    private static final String JDBC_DRIVER = org.h2.Driver.class.getName();

    ConnectionProvider databaseConnector;
    CourseDAO courseDAO;
    private IDatabaseTester databaseTester;

    public CourseDAOTest() throws Exception {
	this.databaseConnector = new ConnectionProvider("application.properties");
	this.courseDAO = new CourseDAO(databaseConnector);
    }

    @BeforeAll
    public static void createSchema() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource("schema.sql");
	File file = new File(url.toURI());
	ConnectionProvider DatabaseConnector = new ConnectionProvider("application.properties");
	RunScript.execute(DatabaseConnector.getConnection(), new FileReader(file));
    }

    @BeforeEach
    void fillTables() throws Exception {
	databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, databaseConnector.getConnection().getMetaData().getURL());
	databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	databaseTester.setDataSet(readDataSet());
	databaseTester.onSetup();
    }

    @Test
    void givenStudentID_whenGetByStudent_thenGetCourses() throws SQLException {
	List<Course> expected = new ArrayList<>();
	expected.add(new Course(1, "Math", "Learn math"));
	expected.add(new Course(2, "History", "Know the past"));

	List<Course> actual = courseDAO.getByStudent(1);

	assertEquals(expected, actual);
    }

    @Test
    void givenTables_whenGetAll_thenGetCourses() throws SQLException {
	List<Course> expected = new ArrayList<>();
	expected.add(new Course(1, "Math", "Learn math"));
	expected.add(new Course(2, "History", "Know the past"));
	expected.add(new Course(3, "Arts", "Understand visuals"));

	List<Course> actual = courseDAO.getAll();

	assertEquals(expected, actual);
    }

    @Test
    void givenCourseId_whenGetById_thenGetCourse() throws SQLException {
	Course expected = new Course(1, "Math", "Learn math");

	Course actual = courseDAO.getById(1).get();

	assertEquals(expected, actual);
    }

    @Test
    void givenNewCourse_whenCreate_thenGetCourse() throws Exception {
	Course expected = new Course("Programming", "Learn Java");
	courseDAO.create(expected);

	Course actual = mapToCourse(
		"select course_id, course_name, course_description from courses where course_name = 'Programming'");
	expected.setId(actual.getId());

	assertEquals(expected, actual);
    }

    @Test
    void givenEmptyName_whenCreate_thenThrowException() {
	assertThrows(SQLException.class, () -> {
	    courseDAO.create(new Course(null, "Description"));
	});
    }

    @Test
    void givenEmptyDescription_whenCreate_thenThrowException() {
	assertThrows(SQLException.class, () -> {
	    courseDAO.create(new Course("Name", null));
	});
    }

    private Course mapToCourse(String query) throws Exception {
	ITable itable = databaseTester.getConnection().createQueryTable("courses", query);
	return new Course(Integer.valueOf(itable.getValue(0, "course_id").toString()),
		itable.getValue(0, "course_name").toString(), itable.getValue(0, "course_description").toString());
    }

    private IDataSet readDataSet() throws Exception {
	ClassLoader classLoader = getClass().getClassLoader();
	String file = classLoader.getResource("testdata.xml").getFile();
	return new FlatXmlDataSetBuilder().build(new FileInputStream(file));
    }
}
