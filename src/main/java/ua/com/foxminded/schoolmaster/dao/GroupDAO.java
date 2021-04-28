package ua.com.foxminded.schoolmaster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ua.com.foxminded.schoolmaster.ConnectionProvider;
import ua.com.foxminded.schoolmaster.domain.Group;

public class GroupDAO {

    private static final String GET_GROUPS = "SELECT group_id, group_name FROM groups ORDER BY group_id";
    private static final String CREATE_GROUP = "INSERT into groups (group_name) VALUES (?)";
    private static final String GROUPS_LESS_THAN = "SELECT g.group_id, g.group_name FROM groups g inner join"
	    + " students s on g.group_id = s.group_id group by g.group_id, g.group_name HAVING count(*) <= ? ORDER BY g.group_id";

    private ConnectionProvider connectionProvider;

    public GroupDAO(ConnectionProvider connectionProvider) {
	this.connectionProvider = connectionProvider;
    }

    public void create(Group group) throws SQLException {
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(CREATE_GROUP,
			Statement.RETURN_GENERATED_KEYS);) {
	    statement.setString(1, group.getName());
	    statement.executeUpdate();
	    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
		generatedKeys.next();
		group.setId(generatedKeys.getInt(1));
	    }
	}
    }

    public List<Group> getByLessThanCount(int studentCount)
	    throws SQLException {
	List<Group> groups = new ArrayList<>();
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(GROUPS_LESS_THAN)) {
	    statement.setInt(1, studentCount);
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    groups.add(mapToGroup(resultSet));
		}
	    }
	}
	return groups;
    }

    public List<Group> getAll() throws SQLException {
	List<Group> groups = new ArrayList<>();
	try (Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = connection.prepareStatement(GET_GROUPS)) {
	    try (ResultSet resultSet = statement.executeQuery();) {
		while (resultSet.next()) {
		    groups.add(mapToGroup(resultSet));
		}
	    }
	}
	return groups;
    }

    private Group mapToGroup(ResultSet resultSet) throws SQLException {
	return new Group(resultSet.getInt("group_id"), resultSet.getString("group_name"));
    }
}
