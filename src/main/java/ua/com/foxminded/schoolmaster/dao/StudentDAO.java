package ua.com.foxminded.schoolmaster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.schoolmaster.ConnectionProvider;
import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Student;

public class StudentDAO {

    private static final String GET_STUDENTS = "SELECT student_id, group_id, first_name, last_name FROM students ORDER BY student_id";
    private static final String GET_STUDENT_BY_ID = "SELECT student_id, group_id, first_name, last_name FROM students WHERE student_id = ?";
    private static final String UPDATE_STUDENT = "UPDATE students set first_name = ?, last_name =?, group_id = ? WHERE student_id = ?";
    private static final String DELETE_STUDENT = "DELETE from students WHERE student_id = ?";
    private static final String ADD_STUDENT_TO_COURSE = "INSERT into students_courses (student_id, course_id) VALUES (?, ?)";
    private static final String DELETE_STUDENT_FROM_COURSE = "DELETE from students_courses WHERE student_id = ? and course_id = ?";
    private static final String CREATE_STUDENT = "INSERT into students (group_id,first_name, last_name) VALUES (?, ?, ?)";
    private static final String GET_STUDENTS_BY_COURSE = "SELECT s.student_id, s.group_id, s.first_name, s.last_name"
	    + " FROM students s left outer join students_courses sc on s.student_id = sc.student_id"
	    + " left outer join courses c on sc.course_id = c.course_id where c.course_name = ?";
    private ConnectionProvider connectionProvider;

    public StudentDAO(ConnectionProvider connectionProvider) {
	this.connectionProvider = connectionProvider;
    }

    public void create(Student student) throws SQLException {
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(CREATE_STUDENT,
			Statement.RETURN_GENERATED_KEYS);) {
	    statement.setObject(1, student.getGroupId());
	    statement.setString(2, student.getFirstName());
	    statement.setString(3, student.getLastName());
	    statement.executeUpdate();
	    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		generatedKeys.next();
		student.setId(generatedKeys.getInt(1));
	    }
	}
    }

    public void update(Student student) throws SQLException {
	try (Connection connection = connectionProvider.getConnection();
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
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(ADD_STUDENT_TO_COURSE);) {
	    statement.setInt(1, student.getId());
	    statement.setInt(2, course.getId());
	    statement.executeUpdate();
	}
    }

    public List<Student> getAll() throws SQLException {
	List<Student> students = new ArrayList<>();

	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_STUDENTS);
		ResultSet resultSet = statement.executeQuery();) {
	    while (resultSet.next()) {
		Student student = extractStudentFromResultSet(resultSet);
		students.add(student);
	    }
	}
	return students;
    }

    public List<Student> getByCourseName(String courseName) throws SQLException {
	List<Student> students = new ArrayList<>();

	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement prepStatement = connection.prepareStatement(GET_STUDENTS_BY_COURSE);) {
	    prepStatement.setString(1, courseName);
	    try (ResultSet resultSet = prepStatement.executeQuery();) {
		while (resultSet.next()) {
		    Student student = extractStudentFromResultSet(resultSet);
		    students.add(student);
		}
	    }
	}
	return students;
    }

    public Optional<Student> getById(int studentId) throws SQLException {
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_STUDENT_BY_ID);) {
	    statement.setInt(1, studentId);
	    try (ResultSet resultSet = statement.executeQuery();) {
		if (resultSet.next()) {
		    return Optional.of(extractStudentFromResultSet(resultSet));
		}
	    }
	}
	return Optional.empty();
    }

    public void delete(Student student) throws SQLException {
	try (Connection connection = connectionProvider.getConnection();
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
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_FROM_COURSE);) {
	    statement.setInt(1, student.getId());
	    statement.setInt(2, course.getId());
	    statement.executeUpdate();
	}
    }

    private Student extractStudentFromResultSet(ResultSet resultSet) throws SQLException {
	return new Student(resultSet.getInt("student_id"),
		resultSet.getString("first_name"),
		resultSet.getString("last_name"),
		resultSet.getInt("group_id"));
    }

}
