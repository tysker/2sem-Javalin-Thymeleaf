package dk.lyngby.controllers;

import dk.lyngby.TokenFactory;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.entities.Role;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.persistence.ConnectionPool;
import dk.lyngby.persistence.auth.AuthFacade;
import io.javalin.http.Context;

public class RegisterController {
    public static void registerController(Context ctx, ConnectionPool connectionPool) throws TokenException, DatabaseException, ApiException {
        // TODO: validate user
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String role = ctx.formParam("role");
        // TODO: check if user exists
        AuthFacade.checkUser(username, connectionPool);
        // TODO: check if role exists
        AuthFacade.checkRole(role);
        // TODO: register user
        AuthFacade.registerUser(username, password, role, connectionPool);
        // TODO: create token
        ClaimBuilder claimBuilder = ApplicationConfig.getClaimBuilder(username, role);
        // TODO: Create a Role
        Role.RoleName userRole = Role.RoleName.valueOf(role);
        String token = TokenFactory.createToken(claimBuilder, "841D8A6C80CBA4FCAD32D5367C18C53B");
    }
}
