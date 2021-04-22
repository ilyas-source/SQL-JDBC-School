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

public class CourseDAO {

    private DatabaseConnector databaseConnector;

    public CourseDAO(DatabaseConnector databaseConnection) {
	databaseConnector = databaseConnection;
    }

    public int create(Course course) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(CREATE_COURSE,
			Statement.RETURN_GENERATED_KEYS);) {
	    statement.setString(1, course.getName());
	    statement.setString(2, course.getDescription());
	    statement.executeUpdate();
	    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		generatedKeys.next();
		return generatedKeys.getInt(1);
	    }
	}
    }

    public List<Course> getAll() throws SQLException {
	List<Course> courses = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_COURSES);
		ResultSet resultSet = statement.executeQuery();) {
	    while (resultSet.next()) {
		Course course = extractCourseFromResultSet(resultSet);
		courses.add(course);
	    }
	}
	return courses;
    }

    public Optional<Course> getById(Integer courseId) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_COURSE_BY_ID);) {
	    statement.setInt(1, courseId);
	    try (ResultSet resultSet = statement.executeQuery();) {
		if (resultSet.next()) {
		    return Optional.of(extractCourseFromResultSet(resultSet));
		}
	    }
	}
	return Optional.empty();
    }

    public List<Course> getByStudent(int studentId) throws SQLException {
	List<Course> courses = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_COURSES_BY_STUDENT_ID);) {
	    statement.setInt(1, studentId);
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    Course course = extractCourseFromResultSet(resultSet);
		    courses.add(course);
		}
	    }
	}
	return courses;
    }

    private Course extractCourseFromResultSet(ResultSet resultSet) throws SQLException {
	return new Course(resultSet.getInt("course_id"),
		resultSet.getString("course_name"),
		resultSet.getString("course_description"));
    }

}
