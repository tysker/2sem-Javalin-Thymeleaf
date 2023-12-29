package dk.lyngby.pages;


import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class IndexPage {
    public static void indexPage(Context ctx){
        Map<String, Object> model = new HashMap<>();
        ctx.render("index.html", model);
    }

}
