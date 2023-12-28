package dk.lyngby.pages;


import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class IndexPage {
    public static void indexPage(Context ctx){
        Map<String, Object> model = new HashMap<>();

//        String logout = ctx.queryParam("logout");
//        if(logout != null){
//            ctx.removeCookie("token");
//            ctx.sessionAttribute("isLoggedIn", false);
//            ctx.sessionAttribute("username", null);
//        }

        ctx.render("index.html", model);
    }

}
