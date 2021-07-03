package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {

    // TODO Complete This Class

    // Creating a Neo4jDAO object to inject into the constructor of ReqHandler as mentioned in the handout
    private Neo4jDAO neo4jDAO;

    // Creating a constructor of ReqHandler as done in code from lecture, with input of Neo4jDAO object to inject
    @Inject
    public ReqHandler(Neo4jDAO neo4jDAO) {
        this.neo4jDAO = neo4jDAO;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // This gives us the HTTP request type we want to work with - GET or PUT
        String request = exchange.getRequestMethod();
//        System.out.println(request);

        try {
            if (request.equals("PUT")) {

                // We pass control to the function handlePut() which takes care of all PUT requests required
                handlePut(exchange);

            } else if (request.equals("GET")) {

                // We instead pass control to the function handleGet() which takes care of all GET requests required
                handleGet(exchange);
            } else {
                System.out.println("This error message came from the original handle() method");
                exchange.sendResponseHeaders(400, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // A method which takes care of all the required PUT requests
    public void handlePut(HttpExchange exchange) throws IOException, JSONException {

        // This gives is the exact endpoint we want - /api/v1 + request method
        String endPoint = exchange.getRequestURI().toString();

        if (endPoint.equals("/api/v1/addActor")) {
            addActor(exchange);
        } else if (endPoint.equals("/api/v1/addMovie")) {

            addMovie(exchange);
        } else if (endPoint.equals("/api/v1/addRelationship")) {

            addRelationship(exchange);

        } else {
            System.out.println("THis error message from handlePut() method");
            exchange.sendResponseHeaders(404, -1);
        }
    }

    // A method to implement the addActor PUT request endpoint, inspired by Sandy's Tut on REST APIs
    public void addActor(HttpExchange exchange) throws IOException, JSONException {

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        // Setting up the body parameters of the `addActor endPoint, namely name and actorId
        String name, actorId;

        // If a name or an actorId is missing send a status code of 400 - BAD REQUEST
        int status = 400;

        // Checking JSONObject to see if parameters are that of `addActor`
        if (deserialized.has("name") && deserialized.has("actorId")) {

            // We need to set up the parameters correctly now
            name = deserialized.getString("name");
            actorId = deserialized.getString("actorId");
            int statusCode = neo4jDAO.addActor(actorId, name); // This is where we need to use Neo4jDAO method of addActor()
            exchange.sendResponseHeaders(statusCode, -1);

        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    public void addMovie(HttpExchange exchange) throws IOException, JSONException {

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        // Setting up the body parameters of the `addMovie endPoint, namely name and movieId
        String name, movieId;

        // If a name or an movieId is missing send a status code of 400 - BAD REQUEST
        int status = 400;

        // Checking JSONObject to see if parameters are that of `addMovie`
        if (deserialized.has("name") && deserialized.has("movieId")) {

            // We need to set up the parameters correctly now
            name = deserialized.getString("name");
            movieId = deserialized.getString("movieId");
            int statusCode = neo4jDAO.addMovie(movieId, name);
            exchange.sendResponseHeaders(statusCode, -1);

        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    public void addRelationship(HttpExchange exchange) throws IOException, JSONException {

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        String movieId, actorId;

        // If a movieId or an actorId is missing send a status code of 400 - BAD REQUEST
        int status = 400;

        // Checking JSONObject to see if parameters are that of `addRelationship`
        if (deserialized.has("movieId") && deserialized.has("actorId")) {

            // We need to set up the parameters correctly now
            movieId = deserialized.getString("movieId");
            actorId = deserialized.getString("actorId");
            int statusCode = neo4jDAO.addRelationship(actorId, movieId);
            exchange.sendResponseHeaders(statusCode, -1);

        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /* This is the part where we handle all the GET requests and all the GET request related methods
     * This is similar to how we have handled PUT requests above */
    public void handleGet(HttpExchange exchange) throws IOException, JSONException {

        // This gives is the exact endpoint we want - /api/v1 + request method
        String endPoint = exchange.getRequestURI().toString();

        if (endPoint.equals("/api/v1/getActor")) {
            getActor(exchange);
        } else if (endPoint.equals("/api/v1/hasRelationship")) {

            hasRelationship(exchange);

        } else if (endPoint.equals("/api/v1/computeBaconNumber")) {

            computeBaconNumber(exchange);
        } else if (endPoint.equals("/api/v1/computeBaconPath")) {

            computeBaconPath(exchange);
        } else {
            System.out.println("This error message from handleGet() method");
            exchange.sendResponseHeaders(404, -1);
        }
    }

    public void getActor(HttpExchange exchange) throws IOException, JSONException {

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        String actorId, actorName;

        if (deserialized.has("actorId")) {

            actorId = deserialized.getString("actorId");

            // This line of code gets the name of the actor
            actorName = neo4jDAO.getActorAsAString(actorId); // Need to implement getActorAsAString() in Neo4jDAO

            if (actorName.trim().equals("")) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            // Otherwise we know that said actor with actorId does EXIST

            // This line of code gets the list of movies that the actor with `actorId` has acted in
            List<String> actedInMovies = neo4jDAO.getActedInMovies(actorId); // getActedInMovies method TBD
            System.out.println("Acted in movies is: " + actedInMovies);
            // This puts everything together in the JSONObject
            JSONObject res = new JSONObject().put("actorId", actorId).put("name", actorName).put("movies",
                    actedInMovies);

            exchange.sendResponseHeaders(200, res.toString().length());

            OutputStream os = exchange.getResponseBody();
            os.write(res.toString().getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(400, -1);

        }
    }


    public void hasRelationship(HttpExchange exchange) throws IOException, JSONException {
        String body = Utils.convert(exchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        String actorId, movieId;

        int status = 400;

        if (deserialized.has("movieId") && deserialized.has("actorId")) {

            // We need to set up the parameters correctly now
            movieId = deserialized.getString("movieId");
            actorId = deserialized.getString("actorId");
            int statusCode = neo4jDAO.relationship(movieId, actorId);

            if (statusCode == 200) {
                JSONObject res = new JSONObject().put("actorId", actorId).put("movieId", movieId).put("hasRelationship", true);
                exchange.sendResponseHeaders(statusCode, res.toString().length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.toString().getBytes());
                os.close();
            } else if (statusCode == 201) {
                JSONObject res = new JSONObject().put("actorId", actorId).put("movieId", movieId).put("hasRelationship", false);
                exchange.sendResponseHeaders(statusCode, res.toString().length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.toString().getBytes());
                os.close();
            }
            // sending either a 404 or 500 response
            else {
                exchange.sendResponseHeaders(statusCode, -1);
            }
        }

        // Send a 400 if request is badly formatted
        else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    public void computeBaconPath(HttpExchange exchange) {

        /* The first few lines are similar to getActor method, where we set up the JSONObject and
        see if the said input exists in our DB and if it does, we get the name of the actor associated
        with the actorId
         */
        try {
            String body = Utils.convert(exchange.getRequestBody());
            JSONObject deserialized = new JSONObject(body);

            String actorId;

            if (deserialized.has("actorId")) {
                List<String> fullPath = new ArrayList<>();
                actorId = deserialized.getString("actorId");
                fullPath = neo4jDAO.baconPath(actorId);
                System.out.println("Path is: " + fullPath);
                JSONObject res = new JSONObject().put("baconPath", fullPath);
                System.out.println("JSON is: " + res);
                exchange.sendResponseHeaders(200, res.toString().length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.toString().getBytes());
                os.close();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void computeBaconNumber(HttpExchange exchange) throws IOException, JSONException {

        /* The first few lines are similar to getActor method, where we set up the JSONObject and
        see if the said input exists in our DB and if it does, we get the name of the actor associated
        with the actorId
         */
        try {
            String body = Utils.convert(exchange.getRequestBody());
            JSONObject deserialized = new JSONObject(body);

            String actorId, actorName;
            if (deserialized.has("actorId")) {

                actorId = deserialized.getString("actorId");

                // This line of code gets the name of the actor
                actorName = neo4jDAO.getActorAsAString(actorId); // Need to implement getActorAsAString() in Neo4jDAO

                if (actorName.trim().equals("")) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                /* At this point we know that input actorId is valid and exists and we can continue
                Check here stores the status code returned by getBaconNumber implemented in Neo4jDAO.java
                 */
                int check = neo4jDAO.getBaconNumber(actorId); // Modified this line to call the correct method

                // If statusCode is 200, this means that valid baconNumber exists and is to be returned in JSON format
                if (check == 200) {

                    // A simple object to get the response from Neo4jDAO.java method to this method
                    JSONObject response = neo4jDAO.getResponse();
                    exchange.sendResponseHeaders(200, response.toString().length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();

                } else {
                    exchange.sendResponseHeaders(check, -1);
                }

            } else {
                exchange.sendResponseHeaders(400, -1);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }
}