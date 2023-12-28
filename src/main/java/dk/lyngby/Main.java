package dk.lyngby;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.ThymeleafConfig;
import dk.lyngby.controllers.AccessManagerController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final AccessManagerController amc = new AccessManagerController();

    public static void main(String[] args) {
        ApplicationConfig.startServer(Javalin.create( config -> {
            JavalinThymeleaf.init(ThymeleafConfig.templateEngine());
            config.staticFiles.add("/public");
            config.accessManager(amc::accessManagerHandler);
        }),7070);

    }
}