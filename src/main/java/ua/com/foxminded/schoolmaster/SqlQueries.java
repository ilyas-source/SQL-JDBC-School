package ua.com.foxminded.schoolmaster;

public final class SqlQueries {

    public static final String GET_COURSES = "SELECT course_id, course_name, course_description FROM courses ORDER BY course_id;";
    public static final String GET_STUDENTS = "SELECT student_id, group_id, first_name, last_name FROM students ORDER BY student_id;";
    public static final String GET_GROUPS = "SELECT group_id, group_name FROM groups ORDER BY group_id;";

    public static final String GET_COURSE_BY_ID = "SELECT course_id, course_name, course_description FROM courses WHERE course_id = ?;";
    public static final String GET_GROUP_BY_ID = "SELECT group_id, group_name FROM groups WHERE group_id = ?;";
    public static final String GET_STUDENT_BY_ID = "SELECT student_id, group_id, first_name, last_name FROM students WHERE student_id = ?;";

    public static final String UPDATE_COURSE = "UPDATE courses set course_name = ?, course_description = ? WHERE course_id = ?;";
    public static final String UPDATE_GROUP = "UPDATE groups set group_name =? WHERE group_id = ?;";
    public static final String UPDATE_STUDENT = "UPDATE students set first_name = ?, last_name =?, group_id = ? WHERE student_id = ?;";

    public static final String DELETE_COURSE = "DELETE from courses WHERE (course_id = ?);";
    public static final String DELETE_GROUP = "DELETE from groups WHERE (group_id = ?);";
    public static final String DELETE_STUDENT = "DELETE from students WHERE student_id = ?;";

    public static final String ADD_STUDENT_TO_COURSE = "INSERT into students_courses (student_id, course_id) VALUES (?, ?);";
    public static final String DELETE_STUDENT_FROM_COURSE = "DELETE from students_courses WHERE student_id = ? and course_id = ?;";
    public static final String GET_COURSES_BY_STUDENT_ID = "SELECT course_id, course_name, course_description FROM courses c where c.course_id in (SELECT course_id FROM students_courses sc WHERE student_id = ?);";

    public static final String GROUPS_LESS_THAN = "SELECT g.group_id, g.group_name FROM groups g inner join"
	    + " students s on g.group_id = s.group_id group by g.group_id, g.group_name HAVING count(*) <= ? ORDER BY g.group_id";

    public static final String CREATE_STUDENT = "INSERT into students (group_id,first_name, last_name) VALUES (?, ?, ?);";
    public static final String CREATE_GROUP = "INSERT into groups (group_name) VALUES (?);";
    public static final String CREATE_COURSE = "INSERT into courses (course_name, course_description) VALUES (?, ?);";

    public static final String GET_STUDENTS_BY_COURSE = "SELECT s.student_id, s.group_id, s.first_name, s.last_name"
	    + " FROM students s left outer join students_courses sc on s.student_id = sc.student_id"
	    + " left outer join courses c on sc.course_id = c.course_id where c.course_name = ?;";
    public static final String GET_STUDENTS_BY_GROUP_ID = "SELECT student_id, group_id, first_name, last_name FROM students WHERE group_id = ?;";
}
