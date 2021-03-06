package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Group;
import ua.com.foxminded.schoolmaster.domain.Student;

public class Main {

    public static void main(String[] args) throws SQLException, IOException, URISyntaxException {
	ConnectionProvider connectionProvider = new ConnectionProvider("application.properties");
	DatabasePopulator databasePopulator = new DatabasePopulator(connectionProvider);

	System.out.println("Creating tables...");
	databasePopulator.executeSqlScript("schema.sql");

	System.out.println("Creating random groups...");
	List<Group> groups = databasePopulator.createRandomGroups(10);

	System.out.println("Inserting into courses...");
	List<Course> courses = databasePopulator.fillTableCourses("courses.txt");

	System.out.println("Generating students...");
	List<Student> students = databasePopulator.generateRandomStudents(200);

	System.out.println("Assigning students to groups...");
	databasePopulator.assignGroups(students, groups, 10, 30);

	System.out.println("Assigning students to courses...");
	databasePopulator.assignCourses(students, courses, 3);

	Menu menu = new Menu(connectionProvider);
	menu.start();
    }
}
