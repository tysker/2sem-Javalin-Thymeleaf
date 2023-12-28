package dk.lyngby.controllers;

import dk.lyngby.TokenFactory;
import dk.lyngby.entities.Role;
import dk.lyngby.entities.User;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.persistence.ConnectionPool;
import dk.lyngby.persistence.auth.AuthMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Optional;
import java.util.Set;

public class AccessManagerController {
    public void accessManagerHandler(Handler handler, Context ctx, Set<? extends RouteRole> permittedRoles) throws Exception {
        System.out.println("AccessManagerController");
        boolean isAuthorized = false;
        String token = ctx.cookie("token");

        if (token != null) {
            User user = getUser(token);
            ctx.sessionAttribute("isLoggedIn", true);
            ctx.sessionAttribute("username", user.getUsername());
            ctx.sessionAttribute("role", user.getRole().getRoleName().toString());
        }

        if (permittedRoles.contains(Role.RoleName.ANYONE)) {
            handler.handle(ctx);
            return;
        } else {
            if (token == null) {
                System.out.println("You have to be logged in to access this page");
                ctx.sessionAttribute("error", "You have to be logged in to access this page");
                ctx.render("login.html");
                return;
            }

            RouteRole role = getRole(ctx);

            if (permittedRoles.contains(role)) {
                isAuthorized = true;
            }
        }

        if (isAuthorized) {
            ctx.status(200);
            handler.handle(ctx);
        } else {
            System.out.println("You have not the right permissions to access this page");
            ctx.sessionAttribute("error", "You have not the right permissions to access this page");
            ctx.render("login.html");
        }
    }

    private Role.RoleName getRole(Context ctx) throws TokenException, DatabaseException {
        String token = ctx.cookie("token");
        User user = getUser(token);
        return Role.RoleName.valueOf(user.getRole().getRoleName().toString());
    }

    private static User getUser(String token) throws TokenException, DatabaseException {
        String usernameFromToken = TokenFactory.getUsernameFromToken(token);
        return AuthMapper.getUser(usernameFromToken, ConnectionPool.getInstance());
    }

}
