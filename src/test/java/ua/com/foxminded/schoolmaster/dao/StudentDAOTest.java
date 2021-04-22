package ua.com.foxminded.schoolmaster.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ua.com.foxminded.schoolmaster.DatabaseConnector;
import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Student;

class StudentDAOTest {

    DatabaseConnector databaseConnector;
    StudentDAO studentDAO;
    private IDatabaseTester databaseTester;

    public StudentDAOTest() throws IOException {
	databaseConnector = new DatabaseConnector("application.properties");
	studentDAO = new StudentDAO(databaseConnector);
    }

    private IDataSet readDataSet() throws Exception {
	ClassLoader classLoader = getClass().getClassLoader();
	String file = classLoader.getResource("testdata.xml").getFile();
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
    void fillTables() throws Exception {
	String jdbcUrl = databaseConnector.getConnection().getMetaData().getURL();
	databaseTester = new JdbcDatabaseTester(org.h2.Driver.class.getName(), jdbcUrl);
	databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	databaseTester.setDataSet(readDataSet());
	databaseTester.onSetup();
    }

    @Test
    void givenTables_whenGetAll_thenGetStudentsList() throws SQLException {
	List<Student> expected = new ArrayList<>();
	expected.add(new Student(1, "Mya", "Lawson", 1));
	expected.add(new Student(2, "Joe", "Woods", 2));
	expected.add(new Student(3, "Ross", "Geller", 3));

	List<Student> actual = studentDAO.getAll();

	assertEquals(expected, actual);
    }

    @Test
    void givenID_whenGetByID_thenGetStudent() throws SQLException {
	Student expected = new Student(1, "Mya", "Lawson", 1);

	Student actual = studentDAO.getById(1).get();

	assertEquals(expected, actual);
    }

    @Test
    void givenStudent_whenUpdate_thenGetUpdatedStudent() throws Exception {
	Student expected = new Student(1, "John", "Doe", 2);
	studentDAO.update(expected);

	Student actual = extractStudentFromQuery(
		"select student_id, group_id, first_name, last_name from students where (student_id = 1);");

	assertEquals(expected, actual);
    }

    @Test
    void givenEmptySecondName_whenUpdate_thenThrowException() throws SQLException {

	assertThrows(SQLException.class, () -> {
	    studentDAO.update(new Student(1, "John", null, 1));
	});
    }

    @Test
    void givenNewStudent_whenCreate_thenGetStudent() throws Exception {
	Student expected = new Student(4, "New", "Student", 1);
	studentDAO.create(expected);

	Student actual = extractStudentFromQuery(
		"select student_id, group_id, first_name, last_name from students where (first_name = 'New');");
	expected.setId(actual.getId());

	assertEquals(expected, actual);
    }

    @Test
    void givenStudent_whenDelete_thenRemains2Students() throws DataSetException, Exception {
	studentDAO.delete(new Student(1, "Mya", "Lawson", 1));
	int expected = 2;

	int actual = databaseTester.getConnection().createQueryTable("students", "select * from students").getRowCount();

	assertEquals(expected, actual);
    }

    @Test
    void givenStudent_whenDelete_thenCascadeDeletedFromCourse() throws DataSetException, Exception {
	studentDAO.delete(new Student(1, "Mya", "Lawson", 1));
	int expected = 4;

	int actual = databaseTester.getConnection()
		.createQueryTable("students_courses", "select * from students_courses").getRowCount();

	assertEquals(expected, actual);
    }

    @Test
    void givenCourseName_whenGetByCourseName_thenGetStudentsList() throws SQLException {
	List<Student> expected = new ArrayList<>();
	expected.add(new Student(1, "Mya", "Lawson", 1));
	expected.add(new Student(2, "Joe", "Woods", 2));

	List<Student> actual = studentDAO.getByCourseName("History");

	assertEquals(expected, actual);
    }

    @Test
    void givenStudent_whenAddToCourse_then1StudentInCourse() throws DataSetException, Exception {
	studentDAO.addToCourse(new Student(1, "Monica", "Geller", 1),
		new Course(3, "Music", "Explore music"));

	int actual = databaseTester.getConnection().createQueryTable("students_courses",
		"select * from students_courses where (student_id = 1) and (course_id = 3)")
		.getRowCount();

	assertEquals(1, actual);
    }

    @Test
    void givenStudent_whenRemoveFromCourse_thenNoStudentsInCourse() throws DataSetException, Exception {
	studentDAO.removeFromCourse(new Student(1, "Mya", "Lawson", 1), new Course(1, "Math", "Learn math"));

	int actual = databaseTester.getConnection()
		.createQueryTable("students_courses",
			"select * from students_courses where (student_id = 1) and (course_id = 1)")
		.getRowCount();

	assertEquals(0, actual);
    }

    private Student extractStudentFromQuery(String query) throws Exception {
	ITable itable = databaseTester.getConnection().createQueryTable("students", query);
	Student student = new Student(Integer.valueOf(itable.getValue(0, "student_id").toString()),
		itable.getValue(0, "first_name").toString(),
		itable.getValue(0, "last_name").toString(),
		null);

	if (itable.getValue(0, "group_id") != null) {
	    student.setGroupId(Integer.valueOf(itable.getValue(0, "group_id").toString()));
	}

	return student;
    }

}
