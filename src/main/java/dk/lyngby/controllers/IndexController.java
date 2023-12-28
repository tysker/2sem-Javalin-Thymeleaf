package dk.lyngby.controllers;

import dk.lyngby.TokenFactory;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.entities.Role;
import dk.lyngby.entities.User;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.DatabaseException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.persistence.ConnectionPool;
import dk.lyngby.persistence.auth.AuthFacade;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class IndexController {

    public static void indexController(Context ctx, ConnectionPool connectionPool) throws TokenException, DatabaseException, ApiException {;
        Map<String, Object> model = new HashMap<>();

        // Get username and password from form
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");


        if (username != null || password != null) {
            // Authenticate user
            User user = AuthFacade.login(username, password, connectionPool);
            Role.RoleName userRole = user.getRole().getRoleName();

            // Create token
            ClaimBuilder claimBuilder = ApplicationConfig.getClaimBuilder(username, userRole.toString());
            String token = TokenFactory.createToken(claimBuilder, "841D8A6C80CBA4FCAD32D5367C18C53B");

            if(token != null){
                ctx.cookie("token", token);
                ctx.sessionAttribute("isLoggedIn", true);
                ctx.sessionAttribute("username", username);
            }
        }

        ctx.render("index.html", model);
    };
}
