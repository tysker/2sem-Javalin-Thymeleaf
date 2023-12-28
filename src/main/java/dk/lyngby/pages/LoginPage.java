package dk.lyngby.pages;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;


public class LoginPage {
    public static void loginPage(Context ctx){
        Map<String, Object> model = new HashMap<>();
        ctx.render("login.html", model);
    }
}
