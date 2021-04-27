package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.schoolmaster.dao.CourseDAO;
import ua.com.foxminded.schoolmaster.dao.GroupDAO;
import ua.com.foxminded.schoolmaster.dao.StudentDAO;
import ua.com.foxminded.schoolmaster.domain.Course;
import ua.com.foxminded.schoolmaster.domain.Group;
import ua.com.foxminded.schoolmaster.domain.Student;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DatabasePopulatorTest {

    @Mock
    ConnectionProvider connectionProvider;

    @Mock
    GroupDAO groupDAO;

    @Mock
    CourseDAO courseDAO;

    @Mock
    StudentDAO studentDAO;

    @InjectMocks
    DatabasePopulator databasePopulator;

    public DatabasePopulatorTest() {
	databasePopulator = new DatabasePopulator(connectionProvider);
    }

    @Test
    void givenFileName_onFillTableCourses_thenGet3Courses() throws SQLException, IOException, URISyntaxException {
	List<Course> expected = new ArrayList<>();
	expected.add(new Course("Math", "Learn math"));
	expected.add(new Course("History", "Know the past"));
	expected.add(new Course("Arts", "Understand visuals"));

	List<Course> actual = databasePopulator.fillTableCourses("courses.txt");

	assertEquals(expected, actual);
    }

    @Test
    void givenQuantity_onCreateRandomGroups_thenGet3Groups() throws SQLException {

	List<Group> actual = databasePopulator.createRandomGroups(3);

	verify(groupDAO, times(3)).create(any());
	assertEquals(3, actual.size());
    }

    @Test
    void givenQuantity_onCreateRandomStudents_thenGet3Students() throws IOException, URISyntaxException, SQLException {

	List<Student> actual = databasePopulator.generateRandomStudents(3);

	verify(studentDAO, times(3)).create(any());
	assertEquals(3, actual.size());
    }

    @Test
    void givenStudentsAndGroups_onAssignGroups_thenGroupsHaveCorrectSize() throws SQLException, IOException, URISyntaxException {
	List<Student> students2 = databasePopulator.generateRandomStudents(200);
	List<Group> groups2 = databasePopulator.createRandomGroups(10);

	AtomicInteger artificialID = new AtomicInteger();

	artificialID.set(0);
	students2.forEach(student -> student.setId(artificialID.incrementAndGet()));

	artificialID.set(0);
	groups2.forEach(group -> group.setId(artificialID.incrementAndGet()));

	databasePopulator.assignGroups(students2, groups2, 10, 30);

	Map<Integer, Long> groupsSize = students2.stream()
		.collect(groupingBy(Student::getGroupId, counting()));

	groupsSize.forEach((k, v) -> assertTrue((v >= 10) && (v <= 30)));
    }
}
