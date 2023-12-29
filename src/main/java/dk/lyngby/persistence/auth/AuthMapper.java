package dk.lyngby.persistence.auth;

import dk.lyngby.entities.Role;
import dk.lyngby.entities.User;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.persistence.ConnectionPool;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@NoArgsConstructor
public class AuthMapper {


    public void checkUser(String username, ConnectionPool connectionPool) throws DatabaseException, ApiException {
        String sql = "select * from \"user\" where username = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    throw new ApiException(400, "User already exists");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex, "Could not check user");
        }
    }

    public void checkRole(String userRole) throws ApiException {
        Role.RoleName[] roleList = Role.RoleName.values();

        boolean roleExists = false;
        for (Role.RoleName role : roleList) {
            if (userRole.equals(role.toString())) {
                roleExists = true;
                break;
            }
        }
        if (!roleExists) throw new ApiException(400, "Role does not exist");

    }

    public void registerUser(String username, String password, String role, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "insert into \"user\" (username, password, role) values (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, hashPassword(password));
                ps.setString(3, role.toUpperCase());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex, "Could not register user");
        }
    }

    public User getUser(String usernameFromToken, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from \"user\" where username = ?";
        User user = new User();
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, usernameFromToken);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    user.setUsername(username);
                    // CREATE CAST (varchar AS roles) WITH INOUT AS IMPLICIT;
                    // https://dzone.com/articles/the-generic-way-to-convert-between-java-and-postgr
                    user.setRole(new Role(Role.RoleName.valueOf(role.toUpperCase())));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex, "Could not get user");
        }
        return user;
    }

    public User verifyUser(String username, String password, ConnectionPool connectionPool) throws ApiException, DatabaseException {

        String sql = "select * from \"user\" where username = ?";
        User user = new User();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String passwordFromDb = rs.getString("password");

                    if (!verifyPassword(password, passwordFromDb)) {
                        throw new ApiException(401, "Invalid credentials");
                    }

                    String usernameFromDb = rs.getString("username");
                    String roleFromDb = rs.getString("role");
                    user.setUsername(usernameFromDb);
                    user.setRole(new Role(Role.RoleName.valueOf(roleFromDb.toUpperCase())));
                } else {
                    throw new ApiException(401, "Invalid credentials");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e, "Could not verify user");
        }
        return user;
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean verifyPassword(String dbPassword, String userPassword) {
        return BCrypt.checkpw(dbPassword, userPassword);
    }
}
