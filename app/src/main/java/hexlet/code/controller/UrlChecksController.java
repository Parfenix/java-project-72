package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class UrlChecksController {
    public static void create(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + urlId + " not found"));

        try {
            var response = Unirest.get(url.getName()).asString();
            var responseBody = response.getBody();
            var doc = Jsoup.parse(responseBody);
            var statusCode = response.getStatus();
            var title = doc.title();

            var h1 = Optional.ofNullable(doc.selectFirst("h1"))
                    .map(tag -> tag.text())
                    .orElse(null);

            var description = Optional.ofNullable(doc.selectFirst("meta[name=description]"))
                    .map(tag -> tag.attr("content"))
                    .orElse(null);

            var createdAt = new Timestamp(System.currentTimeMillis());
            var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId, createdAt);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
        } catch (RuntimeException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "danger");
        }

        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
