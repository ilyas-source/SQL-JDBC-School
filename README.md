## Problem

In spite of having convenient JPA/Hibernate ORM techniques, it is important to know, how they work under the hood: they use plain old JDBC.

## Task
Create an application that inserts/updates/deletes data in the database using JDBC.
Use PostgreSQL DB.

1. Create SQL files with data.

2. Generate test data:

 * 10 groups with randomly generated names. The name should contain 2 characters, hyphen, 2 numbers

 * Create 10 courses (math, biology, etc)

 * 200 students. Take 20 first names and 20 last names and randomly combine them to generate students.

 * Randomly assign students to groups. Each group could contain from 10 to 30 students. It is possible that some groups will be without students or students without groups

 * Create relation MANY-TO-MANY between tables STUDENTS and COURSES. Randomly assign from 1 to 3 courses for each student
 
 3. Write SQL Queries, it should be available from the application menu:

* Find all groups with less or equals student count

* Find all students related to course with given name

* Add new student

* Delete student by STUDENT_ID

* Add a student to the course (from a list)

* Remove the student from one of his or her courses

## Technologies used

* Java

* Plain SQL JDBC

* PostgreSQL

* Junit

* Mockito

* H2 for testing
