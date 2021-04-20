package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, IOException, URISyntaxException {
	DatabaseConnector databaseConnector = new DatabaseConnector();
	DatabasePopulator databasePopulator = new DatabasePopulator(databaseConnector);

	System.out.println("Creating tables...");
	databasePopulator.executeSqlScript("createtables.sql");

	System.out.println("Creating random groups...");
	List<Group> groups = databasePopulator.createRandomGroups(10);

	System.out.println("Inserting into courses...");
	List<Course> courses = databasePopulator.fillTableCourses("courses.txt");

	System.out.println("Generating students...");
	List<Student> students = databasePopulator.generateRandomStudents(200);

	System.out.println("Assigning students to groups...");
	databasePopulator.assignGroups(students, groups);

	System.out.println("Assigning students to courses...");
	databasePopulator.assignCourses(students, courses, 3);

	Menu menu = new Menu(databaseConnector);
	menu.start();
    }
}
