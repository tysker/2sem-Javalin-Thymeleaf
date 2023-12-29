package dk.lyngby.persistence.auth;

import dk.lyngby.entities.User;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.persistence.ConnectionPool;

public class AuthFacade {

    private static final AuthMapper authMapper = new AuthMapper();

    public static User login(String username, String password, ConnectionPool connectionPool) throws DatabaseException, ApiException {
        return authMapper.verifyUser(username, password, connectionPool);
    }

    public static User getUser(String usernameFromToken, ConnectionPool connectionPool) throws DatabaseException {
        return authMapper.getUser(usernameFromToken, connectionPool);
    }

    public static void checkUser(String username, ConnectionPool connectionPool) throws DatabaseException, ApiException {
        authMapper.checkUser(username, connectionPool);
    }

    public static void checkRole(String role) throws ApiException {
        authMapper.checkRole(role);
    }

    public static void registerUser(String username, String password, String role, ConnectionPool connectionPool) throws DatabaseException {
        authMapper.registerUser(username, password, role, connectionPool);
    }
}
