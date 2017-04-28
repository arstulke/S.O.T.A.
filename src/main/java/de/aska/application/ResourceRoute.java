package de.aska.application;

import org.json.JSONArray;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

/**
 * Created ResourceRoute in de.aska.application
 * by ARSTULKE on 26.01.2017.
 */
class ResourceRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String map = request.queryMap("map").value();
        String mode = request.queryMap("mode").value();

        List<String> resources = Application.gameloader.getBuilder(map).getResources(mode);

        JSONArray arr = new JSONArray();
        resources.forEach(s -> arr.put(arr.length(), s));

        response.header("Content-type", "de/aska/application/json");
        return arr.toString();
    }
}
