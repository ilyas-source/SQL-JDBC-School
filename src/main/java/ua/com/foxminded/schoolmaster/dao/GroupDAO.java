package ua.com.foxminded.schoolmaster.dao;

import static ua.com.foxminded.schoolmaster.SqlQueries.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ua.com.foxminded.schoolmaster.DatabaseConnector;
import ua.com.foxminded.schoolmaster.domain.Group;

public class GroupDAO {

    private DatabaseConnector databaseConnector;

    public GroupDAO(DatabaseConnector databaseConnection) {
	databaseConnector = databaseConnection;
    }

    public int create(Group group) throws SQLException {
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(CREATE_GROUP,
			Statement.RETURN_GENERATED_KEYS);) {
	    statement.setString(1, group.getName());
	    statement.executeUpdate();
	    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		generatedKeys.next();
		return generatedKeys.getInt(1);
	    }
	}
    }

    public List<Group> getByLessThanCount(int studentCount)
	    throws SQLException {
	List<Group> groups = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GROUPS_LESS_THAN)) {
	    statement.setInt(1, studentCount);
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    groups.add(new Group(resultSet.getInt("group_id"), resultSet.getString("group_name")));
		}
	    }
	}
	return groups;
    }

    public List<Group> getAll() throws SQLException {
	List<Group> groups = new ArrayList<>();
	try (Connection connection = databaseConnector.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_GROUPS)) {
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    groups.add(new Group(resultSet.getInt("group_id"), resultSet.getString("group_name")));
		}
	    }
	}
	return groups;
    }
}
