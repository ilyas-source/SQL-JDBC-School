package ua.com.foxminded.schoolmaster;

import java.sql.SQLException;
import java.util.Scanner;

import static ua.com.foxminded.schoolmaster.DatabaseAccess.*;

public class Menu {

    private static final String CR = System.lineSeparator();
    private static final String MAIN_MENU_TEXT = "Main menu" + CR
	    + "1. Find all groups with less or equals student count" + CR
	    + "2. Find all students related to course with given name" + CR
	    + "3. Add new student" + CR
	    + "4. Delete student by STUDENT_ID" + CR
	    + "5. Add a student to the course (from a list)" + CR
	    + "6. Remove the student from one of his or her courses" + CR
	    + "Enter choice or 0 to quit:";

    DatabaseConnector databaseConnector;
    Scanner scanner;

    public Menu(DatabaseConnector сonnection) {
	this.databaseConnector = сonnection;
	scanner = new Scanner(System.in);
    }

    public void start() throws SQLException {
	System.out.println(MAIN_MENU_TEXT);
	int menuChoice = readNextInt();
	switch (menuChoice) {
	case 0:
	    System.out.println("Quitting.");
	    System.exit(0);
	case 1:
	    printGroupsByStudentCount();
	    break;
	case 2:
	    printStudentsByCourse();
	    break;
	case 3:
	    addNewStudent();
	    break;
	case 4:
	    callStudentDeletion();
	    break;
	case 5:
	    callAddStudentToCourse();
	    break;
	case 6:
	    callRemoveStudentFromCourse();
	    break;
	default:
	    start();
	    break;
	}

    }

    private void printGroupsByStudentCount() throws SQLException {
	System.out.println(CR + "How many students:");
	int studentCount = readNextInt();
	System.out.println("Groups with less or equal students:");
	getGroupsByLessThanCount(databaseConnector, studentCount)
		.stream()
		.forEach(System.out::println);
	start();
    }

    private void printStudentsByCourse() throws SQLException {
	System.out.println(CR + "Enter one of the course names:");
	getCourses(databaseConnector)
		.stream()
		.forEach(System.out::println);
	scanner.nextLine();
	String courseName = scanner.nextLine();

	getStudentsByCourse(databaseConnector, courseName)
		.stream()
		.forEach(System.out::println);
	start();
    }

    private void addNewStudent() throws SQLException {
	System.out.println(CR + "First name: ");
	scanner.nextLine();
	String firstName = scanner.nextLine();
	System.out.println("Last name: ");
	String lastName = scanner.nextLine();

	createStudent(databaseConnector, new Student(firstName, lastName));

	System.out.println("Student created." + CR);
	start();
    }

    private void callStudentDeletion() throws SQLException {
	System.out.println(CR + "Student ID for deletion or 0 to cancel: ");
	int studentId = readNextInt();

	if (studentId != 0) {
	    if (getStudentById(databaseConnector, studentId).isPresent()) {
		Student student = getStudentById(databaseConnector, studentId).get();
		deleteStudent(databaseConnector, student);
		System.out.println("Student was deleted!" + CR);
	    }
	    start();
	}
    }

    private void callAddStudentToCourse() throws SQLException {
	System.out.println(CR + "Select course ID:");
	getCourses(databaseConnector)
		.stream()
		.forEach(System.out::println);
	int courseId = readNextInt();

	System.out.println("Select student ID:");
	getStudents(databaseConnector).stream().forEach(System.out::println);
	int studentId = readNextInt();

	Course course = getCourseById(databaseConnector, courseId).get();

	Student student = getStudentById(databaseConnector, studentId).get();
	addStudentToCourse(databaseConnector, student, course);

	System.out.println("Student (ID=" + studentId + ") was added to course #" + courseId + CR);
	start();
    }

    private void callRemoveStudentFromCourse() throws SQLException {
	System.out.println(CR + "Choose student ID:");
	getStudents(databaseConnector).stream().forEach(System.out::println);
	int studentId = readNextInt();
	Student student = getStudentById(databaseConnector, studentId).get();

	System.out.println("Course ID to remove:");
	getCoursesByStudent(databaseConnector, studentId)
		.stream()
		.forEach(System.out::println);
	int courseId = readNextInt();
	Course course = getCourseById(databaseConnector, courseId).get();

	removeStudentFromCourse(databaseConnector, student, course);
	System.out.println("Student (ID=" + studentId + ") was removed from course #" + courseId + CR);
	start();
    }

    private int readNextInt() {
	while (!scanner.hasNextInt()) {
	    scanner.next();
	}
	return scanner.nextInt();
    }
}
