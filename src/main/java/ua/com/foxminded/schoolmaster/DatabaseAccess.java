package ua.com.foxminded.schoolmaster;

import static ua.com.foxminded.schoolmaster.SqlQueries.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAccess {

    void databaseAccess() {
    }

    public static int createGroup(DatabaseConnector databaseConnector, Group group) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(CREATE_GROUP,
			Statement.RETURN_GENERATED_KEYS);) {
	    statement.setString(1, group.getName());
	    statement.executeUpdate();
	    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		generatedKeys.next();
		return generatedKeys.getInt(1);
	    }
	}
    }

    public static int createCourse(DatabaseConnector databaseConnector, Course course) throws SQLException {
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

    public static int createStudent(DatabaseConnector databaseConnector, Student student) throws SQLException {
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

    public static void updateStudent(DatabaseConnector databaseConnector, Student student) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT);) {
	    statement.setString(1, student.getFirstName());
	    statement.setString(2, student.getLastName());
	    statement.setInt(3, student.getGroupId());
	    statement.setInt(4, student.getId());
	    statement.executeUpdate();
	}

    }

    public static void addStudentToCourse(DatabaseConnector databaseConnector, Student student, Course course)
	    throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(ADD_STUDENT_TO_COURSE);) {
	    statement.setInt(1, student.getId());
	    statement.setInt(2, course.getId());
	    statement.executeUpdate();
	}
    }

    public static List<Group> getGroupsByLessThanCount(DatabaseConnector databaseConnector, int studentCount)
	    throws SQLException {
	List<Group> groups = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GROUPS_LESS_THAN)) {
	    statement.setInt(1, studentCount);
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    groups.add(new Group(resultSet.getInt("group_id"), resultSet.getString("group_name")));
		}
	    }
	}
	return groups;
    }

    public static List<Course> getCourses(DatabaseConnector databaseConnector) throws SQLException {
	List<Course> courses = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_COURSES);
		ResultSet resultSet = statement.executeQuery();) {
	    while (resultSet.next()) {
		Course course = new Course(resultSet.getInt("course_id"),
			resultSet.getString("course_name"),
			resultSet.getString("course_description"));
		courses.add(course);
	    }
	}
	return courses;
    }

    public static List<Student> getStudents(DatabaseConnector databaseConnector) throws SQLException {
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

    public static List<Student> getStudentsByCourse(DatabaseConnector databaseConnector, String courseName) throws SQLException {
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

    public static Optional<Student> getStudentById(DatabaseConnector databaseConnector, int studentId) throws SQLException {
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

    public static Optional<Course> getCourseById(DatabaseConnector databaseConnector, Integer courseId) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_COURSE_BY_ID);) {
	    statement.setInt(1, courseId);
	    try (ResultSet resultSet = statement.executeQuery();) {
		if (resultSet.next()) {
		    return Optional.of(new Course(resultSet.getInt("course_id"),
			    resultSet.getString("course_name"),
			    resultSet.getString("course_description")));
		}
	    }
	}
	return Optional.empty();
    }

    public static void deleteStudent(DatabaseConnector databaseConnector, Student student) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT);) {
	    statement.setInt(1, student.getId());
	    int rowsAffected = statement.executeUpdate();
	    if (rowsAffected == 0) {
		throw new SQLException("Student was not found in database!");
	    }
	}
    }

    public static void removeStudentFromCourse(DatabaseConnector databaseConnector, Student student, Course course)
	    throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_FROM_COURSE);) {
	    statement.setInt(1, student.getId());
	    statement.setInt(2, course.getId());
	    statement.executeUpdate();
	}
    }

    public static List<Course> getCoursesByStudent(DatabaseConnector databaseConnector, int studentId) throws SQLException {
	List<Course> courses = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_COURSES_BY_STUDENT_ID);) {
	    statement.setInt(1, studentId);
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    Course course = new Course(resultSet.getInt("course_id"),
			    resultSet.getString("course_name"),
			    resultSet.getString("course_description"));
		    courses.add(course);
		}
	    }
	}
	return courses;
    }

}
