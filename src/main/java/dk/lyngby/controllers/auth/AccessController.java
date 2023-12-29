package dk.lyngby.controllers.auth;

import dk.lyngby.TokenFactory;
import dk.lyngby.entities.Role;
import dk.lyngby.entities.User;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.persistence.ConnectionPool;
import dk.lyngby.persistence.auth.AuthFacade;
import dk.lyngby.persistence.auth.AuthMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Set;

public class AccessController {
    public void accessManagerHandler(Handler handler, Context ctx, Set<? extends RouteRole> permittedRoles) throws Exception {
        boolean isAuthorized = false;

        if (permittedRoles.contains(Role.RoleName.ANYONE)) {
            handler.handle(ctx);
            return;
        } else {

            User user;
            String token = ctx.cookie("token");

            if (token != null) {
                user = getUser(token);

                if (user.getUsername() == null) {
                    ctx.sessionAttribute("error", "Wrong information provided");
                    ctx.render("login.html");
                    return;
                }

                ctx.sessionAttribute("isLoggedIn", true);
                ctx.sessionAttribute("username", user.getUsername());
                ctx.sessionAttribute("role", user.getRole().getRoleName().toString());
            } else {
                ctx.sessionAttribute("isLoggedIn", false);
                ctx.sessionAttribute("username", null);
                ctx.sessionAttribute("role", null);

                ctx.sessionAttribute("error", "You have to be logged in to access this page");
                ctx.render("login.html");
                return;
            }

            if (permittedRoles.contains(user.getRole().getRoleName())) {
                isAuthorized = true;
            }
        }

        if (isAuthorized) {
            ctx.status(200);
            handler.handle(ctx);
        } else {
            ctx.sessionAttribute("error", "You have not the right permissions to access this page");
            ctx.render("login.html");
        }
    }

    private User getUser(String token) throws TokenException, DatabaseException {
        String usernameFromToken = TokenFactory.getUsernameFromToken(token);
        return AuthFacade.getUser(usernameFromToken, ConnectionPool.getInstance());
    }

}
