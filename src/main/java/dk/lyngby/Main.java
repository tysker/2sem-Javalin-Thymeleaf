package dk.lyngby;

import dk.lyngby.config.ThymeleafConfig;
import dk.lyngby.controllers.auth.AccessController;
import dk.lyngby.controllers.auth.AccessMiddleware;
import dk.lyngby.controllers.exceptions.ExceptionController;
import dk.lyngby.entities.Role;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.pages.IndexPage;
import dk.lyngby.pages.LoginPage;
import dk.lyngby.pages.TestPage;
import dk.lyngby.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static final ExceptionController exceptionController = new ExceptionController();
    private static final AccessController amc = new AccessController();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.accessManager(amc::accessManagerHandler);
            config.staticFiles.add("/public");
            JavalinThymeleaf.init(ThymeleafConfig.templateEngine());
        }).start(7070);

        // Middleware before
        app.before(AccessMiddleware::cleanUp);
        app.before(ctx -> AccessMiddleware.register(ctx, connectionPool));
        app.before(ctx -> AccessMiddleware.login(ctx, connectionPool));

        // Routing
        app.get("/", IndexPage::indexPage, Role.RoleName.ANYONE);
        app.get("/login", LoginPage::loginPage, Role.RoleName.ANYONE);
        app.get("/register", LoginPage::registerPage, Role.RoleName.ANYONE);
        app.get("/test", TestPage::testPage, Role.RoleName.USER);
        // app.get("/*", ctx -> ctx.render("pageNotFound.html"), Role.RoleName.ANYONE); // does not work with Thymeleaf and css

        // Exception handling
        app.exception(ApiException.class, exceptionController::apiExceptionHandler);
        app.exception(AuthorizationException.class, exceptionController::exceptionHandlerNotAuthorized);
        app.exception(TokenException.class, exceptionController::tokenExceptionHandler);
        app.exception(Exception.class, exceptionController::exceptionHandler);

        // Middleware after
        app.after(AccessMiddleware::logout);

    }
}