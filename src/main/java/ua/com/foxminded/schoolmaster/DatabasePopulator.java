package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Group;
import ua.com.foxminded.schoolmaster.domain.Student;

import static ua.com.foxminded.schoolmaster.NamingGenerator.*;
import static ua.com.foxminded.schoolmaster.DatabaseAccess.*;

public class DatabasePopulator {

    private static final String CR = System.lineSeparator();
    private DatabaseConnector databaseConnector;
    private FileReader fileReader;

    public DatabasePopulator(DatabaseConnector databaseConnector) {
	this.databaseConnector = databaseConnector;
	fileReader = new FileReader();
    }

    public void executeSqlScript(String fileName) throws SQLException, URISyntaxException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(getFileLines(fileName));) {
	    statement.execute();
	} catch (SQLException | IOException e) {
	    throw new SQLException("Couldn't execute SQL query", e);
	}
    }

    public List<Group> createRandomGroups(int quantity) throws SQLException {
	List<Group> groups = new ArrayList<>();
	for (int i = 0; i < quantity; i++) {
	    Group group = new Group(generateGroupName());
	    groups.add(group);
	    int assignedID = createGroup(databaseConnector, group);
	    group.setId(assignedID);
	}
	return groups;
    }

    public List<Course> fillTableCourses(String fileName) throws SQLException, IOException, URISyntaxException {
	List<Course> courses = new ArrayList<>();
	for (String line : getFileLines(fileName).split(CR)) {
	    String[] courseData = line.split("_");
	    Course course = new Course(courseData[0], courseData[1]);
	    courses.add(course);
	    int assignedID = createCourse(databaseConnector, course);
	    course.setId(assignedID);
	}
	return courses;
    }

    public List<Student> generateRandomStudents(int quantity) throws SQLException {
	Supplier<Student> randomStudent = () -> {
	    try {
		return new Student(
			getRandomStringFromFile("firstnames.txt"),
			getRandomStringFromFile("lastnames.txt"));
	    } catch (IOException | URISyntaxException e) {
		e.printStackTrace();
	    }
	    return new Student();
	};
	List<Student> students = Stream.generate(randomStudent)
		.limit(quantity)
		.collect(toList());
	for (Student student : students) {
	    int assignedID = createStudent(databaseConnector, student);
	    student.setId(assignedID);
	}
	return students;
    }

    public void assignGroups(List<Student> students, List<Group> groups) throws SQLException {
	for (Student student : students) {
	    student.setGroupId(groups.get(getRandomNumber(0, groups.size() - 1)).getId());
	}

	Map<Integer, Long> groupsSize = students.stream()
		.collect(groupingBy(Student::getGroupId, counting()));

	for (Student student : students) {
	    Long studentsInGroup = groupsSize.get(student.getGroupId());
	    if (studentsInGroup >= 10 && studentsInGroup <= 30) {
		updateStudent(databaseConnector, student);
	    } else {
		student.setGroupId(null);
	    }
	}
    }

    public void assignCourses(List<Student> students, List<Course> courses, int quantity)
	    throws SQLException {
	for (Student student : students) {
	    Collections.shuffle(courses);
	    for (Course course : courses.subList(0, getRandomNumber(1, quantity))) {
		addStudentToCourse(databaseConnector, student, course);
	    }
	}
    }

    private int getRandomNumber(int min, int max) {
	return (int) ((Math.random() * (max - min + 1)) + min);
    }

    private String getFileLines(String fileName) throws IOException, URISyntaxException {
	URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
	Path path = Paths.get(url.toURI());
	return fileReader.readFile(path);
    }
}
