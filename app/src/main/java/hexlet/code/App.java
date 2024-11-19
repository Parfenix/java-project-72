package hexlet.code;

import io.javalin.Javalin;

import java.io.IOException;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        var port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
        app.start(port);
    }

    public static Javalin getApp() throws IOException, SQLException {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }
}
