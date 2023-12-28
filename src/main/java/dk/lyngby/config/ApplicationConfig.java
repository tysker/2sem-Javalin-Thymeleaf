package dk.lyngby.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.lyngby.TokenFactory;
import dk.lyngby.controllers.AccessManagerController;
import dk.lyngby.controllers.IndexController;
import dk.lyngby.controllers.exceptions.ExceptionController;
import dk.lyngby.dto.UserDto;
import dk.lyngby.entities.Role;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.pages.IndexPage;
import dk.lyngby.pages.LoginPage;
import dk.lyngby.pages.TestPage;
import dk.lyngby.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.validation.ValidationException;

import java.util.Map;

public class ApplicationConfig {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static final ExceptionController exceptionController = new ExceptionController();

    public static void startServer(Javalin app, int port) {

        // Pages
        app.get("/", IndexPage::indexPage, Role.RoleName.ANYONE);
        app.get("/login", LoginPage::loginPage, Role.RoleName.ANYONE);
        app.get("/test", TestPage::testPage, Role.RoleName.USER);
        app.get("/*", ctx -> ctx.render("pageNotFound.html"), Role.RoleName.ANYONE);

        // Controllers
        app.post("/", ctx -> IndexController.indexController(ctx, connectionPool), Role.RoleName.ANYONE);

        // Exception handling
        app.exception(ApiException.class, exceptionController::apiExceptionHandler);
        app.exception(AuthorizationException.class, exceptionController::exceptionHandlerNotAuthorized);
        app.exception(TokenException.class, exceptionController::tokenExceptionHandler);
        app.exception(Exception.class, exceptionController::exceptionHandler);

        app.start(port);
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    public static UserDto verifyToken(String token, String user, String role) throws TokenException {
        Gson gson = new GsonBuilder().create();
        String extractedUser = TokenFactory.verifyToken(token, "841D8A6C80CBA4FCAD32D5367C18C53B", getClaimBuilder(user, role));
        return gson.fromJson(extractedUser, UserDto.class);
    }

    public static ClaimBuilder getClaimBuilder(String username, String role) {
        return ClaimBuilder.builder()
                .issuer("cphbusiness")
                .audience("cphbusiness")
                .claimSet(Map.of("username", username, "role", role))
                .expirationTime(Long.parseLong("3600000"))
                .issueTime(3600000L)
                .build();
    }
}
