package dk.lyngby.controllers.auth;

import dk.lyngby.TokenFactory;
import dk.lyngby.utils.ApplicationUtils;
import dk.lyngby.entities.Role;
import dk.lyngby.entities.User;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.persistence.ConnectionPool;
import dk.lyngby.persistence.auth.AuthFacade;
import io.javalin.http.Context;

public class AccessMiddleware {

    public static void login(Context ctx, ConnectionPool connectionPool) throws DatabaseException, ApiException, TokenException {

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        String login = ctx.formParam("login");

        if (login != null && login.equals("login") && username != null || password != null) {
            // Authenticate user
            User user = AuthFacade.login(username, password, connectionPool);
            Role.RoleName userRole = user.getRole().getRoleName();

            // Create token
            ClaimBuilder claimBuilder = ApplicationUtils.getClaimBuilder(username, userRole.toString());
            String token = TokenFactory.createToken(claimBuilder, "841D8A6C80CBA4FCAD32D5367C18C53B");

            if(token != null){
                ctx.cookie("token", token);
                ctx.sessionAttribute("isLoggedIn", true);
                ctx.sessionAttribute("username", username);
            }
            ctx.redirect("/");
        }
    }

    public static void register(Context ctx, ConnectionPool connectionPool) throws TokenException, DatabaseException, ApiException {
        // validate user
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String role = "USER";

        String register = ctx.formParam("register");

        if(register != null && register.equals("register") && username != null && password != null) {
            // check if user exists
            AuthFacade.checkUser(username, connectionPool);
            // check if role exists
            AuthFacade.checkRole(role);
            // register user
            AuthFacade.registerUser(username, password, role, connectionPool);
            // create token
            ClaimBuilder claimBuilder = ApplicationUtils.getClaimBuilder(username, role);
            // Create a Role
            Role.RoleName userRole = Role.RoleName.valueOf(role);
            String token = TokenFactory.createToken(claimBuilder, "841D8A6C80CBA4FCAD32D5367C18C53B");
            ctx.cookie("token", token);
            ctx.sessionAttribute("isLoggedIn", true);
            ctx.sessionAttribute("username", username);
            ctx.sessionAttribute("role", userRole);
            ctx.redirect("/");
        }
    }

    public static void logout(Context ctx) {
        String logout = ctx.queryParam("logout");
        if(logout != null){
            ctx.removeCookie("token");
            ctx.sessionAttribute("isLoggedIn", false);
            ctx.sessionAttribute("username", null);
            ctx.sessionAttribute("role", null);
            ctx.render("index.html");
        }
    }

    public static void cleanUp(Context ctx) {
        if (ctx.sessionAttribute("isLoggedIn") == null) {
            ctx.sessionAttribute("isLoggedIn", false);
            ctx.sessionAttribute("username", "");
            ctx.sessionAttribute("role", "");
        }
    }
}
