package dk.lyngby.pages;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;


public class LoginPage {
    public static void loginPage(Context ctx){
        ctx.render("login.html");
    }

    public static void registerPage(Context ctx) {
        ctx.render("register.html");
    }
}
