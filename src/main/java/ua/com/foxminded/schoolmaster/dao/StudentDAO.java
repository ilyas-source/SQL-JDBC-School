package ua.com.foxminded.schoolmaster.dao;

import static ua.com.foxminded.schoolmaster.SqlQueries.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.schoolmaster.DatabaseConnector;
import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Student;

public class StudentDAO {

    private DatabaseConnector databaseConnector;

    public StudentDAO(DatabaseConnector databaseConnection) {
	databaseConnector = databaseConnection;
    }

    public int create(Student student) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(CREATE_STUDENT,
			Statement.RETURN_GENERATED_KEYS);) {
	    statement.setObject(1, student.getGroupId());
	    statement.setString(2, student.getFirstName());
	    statement.setString(3, student.getLastName());
	    statement.executeUpdate();
	    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		generatedKeys.next();
		return generatedKeys.getInt(1);
	    }
	}
    }

    public void update(Student student) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT);) {
	    statement.setString(1, student.getFirstName());
	    statement.setString(2, student.getLastName());
	    statement.setInt(3, student.getGroupId());
	    statement.setInt(4, student.getId());
	    statement.executeUpdate();
	}
    }

    public void addToCourse(Student student, Course course)
	    throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(ADD_STUDENT_TO_COURSE);) {
	    statement.setInt(1, student.getId());
	    statement.setInt(2, course.getId());
	    statement.executeUpdate();
	}
    }

    public List<Student> getAll() throws SQLException {
	List<Student> students = new ArrayList<>();

	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_STUDENTS);
		ResultSet resultSet = statement.executeQuery();) {
	    while (resultSet.next()) {
		Student student = new Student(resultSet.getInt("student_id"),
			resultSet.getString("first_name"),
			resultSet.getString("last_name"),
			resultSet.getInt("group_id"));
		students.add(student);
	    }
	}
	return students;
    }

    public List<Student> getByCourse(String courseName) throws SQLException {
	List<Student> students = new ArrayList<>();

	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement(GET_STUDENTS_BY_COURSE);) {
	    prepStatement.setString(1, courseName);
	    try (ResultSet resultSet = prepStatement.executeQuery();) {
		while (resultSet.next()) {
		    Student student = new Student(resultSet.getInt("student_id"),
			    resultSet.getString("first_name"),
			    resultSet.getString("last_name"),
			    resultSet.getInt("group_id"));
		    students.add(student);
		}
	    }
	}
	return students;
    }

    public Optional<Student> getById(int studentId) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_STUDENT_BY_ID);) {
	    statement.setInt(1, studentId);
	    try (ResultSet resultSet = statement.executeQuery();) {
		if (resultSet.next()) {
		    return Optional.of(new Student(resultSet.getInt("student_id"),
			    resultSet.getString("first_name"),
			    resultSet.getString("last_name"),
			    resultSet.getInt("group_id")));
		}
	    }
	}
	return Optional.empty();
    }

    public void delete(Student student) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT);) {
	    statement.setInt(1, student.getId());
	    int rowsAffected = statement.executeUpdate();
	    if (rowsAffected == 0) {
		throw new SQLException("Student was not found in database!");
	    }
	}
    }

    public void removeFromCourse(Student student, Course course)
	    throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_FROM_COURSE);) {
	    statement.setInt(1, student.getId());
	    statement.setInt(2, course.getId());
	    statement.executeUpdate();
	}
    }
}
