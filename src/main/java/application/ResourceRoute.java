package application;

import org.json.JSONArray;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Set;

/**
 * Created ResourceRoute in application
 * by ARSTULKE on 26.01.2017.
 */
public class ResourceRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String map = request.queryMap("map").value();
        String mode = request.queryMap("mode").value();

        Set<String> resources = Application.gameloader.getResources(map, mode);

        JSONArray arr = new JSONArray();
        resources.forEach(s -> arr.put(arr.length(), s));

        response.header("Content-type", "application/json");
        return arr.toString();
    }
}
