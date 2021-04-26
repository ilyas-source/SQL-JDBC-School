package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

import javax.swing.ListModel;

import ua.com.foxminded.schoolmaster.dao.CourseDAO;
import ua.com.foxminded.schoolmaster.dao.GroupDAO;
import ua.com.foxminded.schoolmaster.dao.StudentDAO;
import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Group;
import ua.com.foxminded.schoolmaster.domain.Student;

import static ua.com.foxminded.schoolmaster.NamingGenerator.*;

public class DatabasePopulator {

    private static final String CR = System.lineSeparator();
    private static final int MINIMUM_STUDENTS = 10;
    private static final int MAXIMUM_STUDENTS = 30;
    private ConnectionProvider connectionProvider;
    private FileReader fileReader;
    CourseDAO courseDAO;
    StudentDAO studentDAO;
    GroupDAO groupDAO;

    public DatabasePopulator(ConnectionProvider connectionProvider) {
	this.connectionProvider = connectionProvider;
	this.groupDAO = new GroupDAO(connectionProvider);
	this.studentDAO = new StudentDAO(connectionProvider);
	this.courseDAO = new CourseDAO(connectionProvider);
	fileReader = new FileReader();
    }

    public void executeSqlScript(String fileName) throws SQLException, URISyntaxException {
	try (Connection connection = connectionProvider.getConnection();
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
	    groupDAO.create(group);
	}
	return groups;
    }

    public List<Course> fillTableCourses(String fileName) throws SQLException, IOException, URISyntaxException {
	List<Course> courses = new ArrayList<>();
	for (String line : getFileLines(fileName).split(CR)) {
	    String[] courseData = line.split("_");
	    Course course = new Course(courseData[0], courseData[1]);
	    courses.add(course);
	    courseDAO.create(course);
	}
	return courses;
    }

    public List<Student> generateRandomStudents(int quantity) throws IOException, URISyntaxException {
	List<String> firstNames = getStringsFromFile("firstnames.txt");
	List<String> lastNames = getStringsFromFile("firstnames.txt");
	Supplier<Student> randomStudent = () -> new Student(getRandomString(firstNames), getRandomString(lastNames));
	return Stream.generate(randomStudent)
		.limit(quantity)
		.peek(student -> {
		    try {
			studentDAO.create(student);
		    } catch (SQLException e) {
			e.printStackTrace();
		    }
		})
		.collect(toList());
    }

    public void assignGroups(List<Student> students, List<Group> groups) throws SQLException {
	Map<Integer, Long> groupsSize = students.stream()
		.peek(student -> student.setGroupId(groups.get(getRandomNumber(0, groups.size() - 1)).getId()))
		.collect(groupingBy(Student::getGroupId, counting()));

	for (Student student : students) {
	    Long studentsInGroup = groupsSize.get(student.getGroupId());
	    if (studentsInGroup >= MINIMUM_STUDENTS && studentsInGroup <= MAXIMUM_STUDENTS) {
		studentDAO.update(student);
	    } else {
		student.setGroupId(null);
	    }
	}
    }

    public void assignCourses(List<Student> students, List<Course> courses, int quantity)
	    throws SQLException {
	for (Student student : students) {
	    for (Course course : selectRandomCourses(courses, getRandomNumber(1, quantity))) {
		studentDAO.addToCourse(student, course);
	    }
	}
    }

    private List<Course> selectRandomCourses(List<Course> courses, int quantity) {
	Random random = new Random();
	if (quantity >= courses.size()) {
	    return courses;
	}

	List<Course> selectedCourses = new ArrayList<>();
	int listSize = courses.size();

	while (selectedCourses.size() < quantity) {
	    int randomIndex = random.nextInt(listSize);
	    Course course = courses.get(randomIndex);

	    if (!selectedCourses.contains(course)) {
		selectedCourses.add(course);
	    }
	}
	return selectedCourses;
    }

    private int getRandomNumber(int min, int max) {
	return (int) ((Math.random() * (max - min + 1)) + min);
    }

    private String getFileLines(String fileName) throws IOException, URISyntaxException {
	URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
	Path path = Paths.get(url.toURI());
	return fileReader.readFile(path);
    }

    private List<String> getStringsFromFile(String fileName) throws IOException, URISyntaxException {
	URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
	Path path = Paths.get(url.toURI());
	return Files.lines(path).collect(toList());
    }

    private String getRandomString(List<String> strings) {
	Random rand = new Random();
	return strings.get(rand.nextInt(strings.size()));
    }
}
