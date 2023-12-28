package dk.lyngby.controllers.exceptions;

import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExceptionController {
    private final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);
    public void exceptionHandlerNotAuthorized(AuthorizationException e, Context ctx) {
        LOGGER.error(ctx.attribute("requestInfo") + " " + ctx.res().getStatus() + " " + e.getMessage());
        Map<String, Object> model = new HashMap<>();
        model.put("message", e.getMessage());
        ctx.status(e.getStatusCode());
        ctx.render("error.html", model);
    }

    public void apiExceptionHandler(ApiException e, Context ctx) {
        LOGGER.error(ctx.attribute("requestInfo") + " " + ctx.res().getStatus() + " " + e.getMessage());
        Map<String, Object> model = new HashMap<>();
        model.put("message", e.getMessage());
        ctx.status(e.getStatusCode());
        ctx.render("error.html", model);
    }

    public void exceptionHandler(Exception e, Context ctx) {
        LOGGER.error(ctx.attribute("requestInfo") + " " + ctx.res().getStatus() + " " + e.getMessage());
        Map<String, Object> model = new HashMap<>();
        model.put("message", e.getMessage());
        ctx.status(500);
        ctx.render("error.html", model);
    }

    public void tokenExceptionHandler(TokenException e, Context context) {
        LOGGER.error(context.attribute("requestInfo") + " " + context.res().getStatus() + " " + e.getMessage());
        Map<String, Object> model = new HashMap<>();
        model.put("message", e.getMessage());
        context.status(400);
        context.render("error.html", model);
    }
}
