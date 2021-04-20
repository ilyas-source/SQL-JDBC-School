package ua.com.foxminded.schoolmaster;

public class Student {

    private int id;
    private String firstName;
    private String lastName;
    private Integer groupId;

    public Student(int id, String firstName, String lastName, Integer groupId) {
	this.id = id;
	this.firstName = firstName;
	this.lastName = lastName;
	this.groupId = groupId;
    }

    public Student(String firstName, String lastName) {
	this.firstName = firstName;
	this.lastName = lastName;
    }

    public Student() {
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public Integer getGroupId() {
	return groupId;
    }

    public void setGroupId(Integer groupId) {
	this.groupId = groupId;
    }

    @Override
    public int hashCode() {
	return Integer.valueOf(this.getId()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
	if (o == this) {
	    return true;
	}
	if (!(o instanceof Student)) {
	    return false;
	}
	Student input = (Student) o;

	if (!this.getFirstName().equals(input.getFirstName())) {
	    return false;
	}
	if (!this.getLastName().equals(input.getLastName())) {
	    return false;
	}

	if (!this.getGroupId().equals(input.getGroupId())) {
	    return false;
	}

	return this.getId() == input.getId();
    }

    @Override
    public String toString() {
	return this.getId() + ": " + this.getFirstName() + " " + getLastName() + " (group #" + this.getGroupId() + ")";
    }

}
