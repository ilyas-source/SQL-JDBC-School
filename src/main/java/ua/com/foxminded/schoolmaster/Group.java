package ua.com.foxminded.schoolmaster;

public class Group {

    private int id;
    private String name;

    public Group(int id, String name) {
	this.id = id;
	this.name = name;
    }

    public Group(String name) {
	this.name = name;
    }

    public int getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setName(String name) {
	this.name = name;
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
	if (!(o instanceof Group)) {
	    return false;
	}
	Group input = (Group) o;

	if (!this.getName().equals(input.getName())) {
	    return false;
	}

	return this.getId() == input.getId();
    }

    @Override
    public String toString() {
	return this.id + ": " + this.name;
    }

}
