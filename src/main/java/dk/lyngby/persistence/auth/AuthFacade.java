package dk.lyngby.persistence.auth;

import dk.lyngby.entities.User;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.persistence.ConnectionPool;

public class AuthFacade {

    public static User login(String username, String password, ConnectionPool connectionPool) throws DatabaseException, ApiException {
        return AuthMapper.verifyUser(username, password, connectionPool);
    }

    public static User getUser(String usernameFromToken, ConnectionPool connectionPool) throws DatabaseException {
        return AuthMapper.getUser(usernameFromToken, connectionPool);
    }

    public static void checkUser(String username, ConnectionPool connectionPool) throws DatabaseException, ApiException {
        AuthMapper.checkUser(username, connectionPool);
    }

    public static void checkRole(String role) throws ApiException {
        AuthMapper.checkRole(role);
    }

    public static void registerUser(String username, String password, String role, ConnectionPool connectionPool) throws DatabaseException {
        AuthMapper.registerUser(username, password, role, connectionPool);
    }
}
