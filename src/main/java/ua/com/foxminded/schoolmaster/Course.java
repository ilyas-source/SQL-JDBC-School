package ua.com.foxminded.schoolmaster;

public class Course {

    private int id;
    private String name;
    private String description;

    public Course(int id, String name, String description) {
	this.id = id;
	this.name = name;
	this.description = description;
    }

    public Course(String name, String description) {
	this.name = name;
	this.description = description;
    }

    public int getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setDescription(String description) {
	this.description = description;
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
	if (!(o instanceof Course)) {
	    return false;
	}
	Course input = (Course) o;

	if (!this.getName().equals(input.getName())) {
	    return false;
	}

	if (!this.getDescription().equals(input.getDescription())) {
	    return false;
	}
	return this.getId() == input.getId();
    }

    @Override
    public String toString() {
	return this.id + ": " + this.name;
    }
}
