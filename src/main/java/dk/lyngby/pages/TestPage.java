package dk.lyngby.pages;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class TestPage {

    public static void testPage(Context ctx){
        Map<String, Object> model = new HashMap<>();
        ctx.render("test.html", model);
    }
}
