package ca.utoronto.utm.mcs;

import javax.inject.Inject;

import org.json.JSONObject;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Value;

import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// All your database transactions or queries should
// go in this class
public class Neo4jDAO implements AutoCloseable{
    // TODO Complete This Class

    // A private instance of a Neo4j Driver so that we can use its method
    private Driver driver;

    //  A private instance of a JSON object to help get the required JSON response body format
    private JSONObject responseBody;

    // To inject the driver dependency into Neo4jDAO as mentioned in the handout
    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
    }

    public void close() throws Exception{
        driver.close();
    }

    /*
     * This method returns:
     * 200 - the movie was successfully added
     * 400 - A movie with the given id already exists in the database
     * 500 - Server error
     */
    public int addMovie(String id, String name) {
        // Implement add movie here
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            if(moviePresent(id)) {
                // return a 400 if movie already present
                return 400;
            }
            else {
                // add movie to the database and return a 200
                String query = "CREATE (m: Movie {name:'%s', id:'%s'})".formatted(name, id);
                tx.run(query);
                tx.commit();
                return 200;
            }
        }
        catch(Exception e) {
            // Throw an error
            e.printStackTrace();
            return 500;
        }
    }

    /*
     * This method returns:
     * 200 - The actor was successfully added
     * 400 - An actor with the given id already exists in the database
     * 500 - Server error
     */
    public int addActor(String id, String name) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            // Check if actor already present in the database, if so return a 400
            if(actorPresent(id)) {
                return 400;
            }
            else {
                // add actor to database and return a 400
                String query = "CREATE (a: Actor {name:'%s', id:'%s'})".formatted(name, id);
                tx.run(query);
                tx.commit();
                return 200;
            }

        }
        catch(Exception e) {
            // Throw an error
            e.printStackTrace();
            return 500;
        }
    }

    /*
     * This methods returns:
     * 200 - Relationship successfully added
     * 404 - Given actor/movie does not exist
     * 400 - A relationship already exists between the given actor and movie
     * 500 - Server error
     */
    public int addRelationship(String actorId, String movieId) {
        // Implement add Relationship
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            // return a 404 if actor or movie not present
            if(!actorPresent(actorId) || !moviePresent(movieId)) {
                return 404;
            }
            else {
                // if a relationship already exists between the actor and the movie return a 400
                if(hasRelationship(movieId, actorId)) {
                    return 400;
                }
                else {
                    // add a relationship and a return a 200
                    String query = "MATCH (a: Actor), (m: Movie) WHERE a.id = '%s' AND m.id = '%s' CREATE (a)-[:ACTED_IN]->(m)".formatted(actorId, movieId);
                    tx.run(query);
                    tx.commit();
                    return 200;
                }
            }
        }
        catch(Exception e) {
            // Throw an error
            e.printStackTrace();
            return 500;
        }
    }

    /*
     * This method returns:
     * True - if an actor with the given id is already present in the database
     * False - if an actor with the given id is not present in the database
     */
    public boolean actorPresent(String id) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (a: Actor) WHERE a.id = '%s' RETURN a".formatted(id);
            Result result = tx.run(query);
            Boolean isPresent = result.hasNext();
            tx.commit();
            return isPresent;
        }
    }

    /*
     * This method returns:
     * True - if a movie with the given id is already present in the database
     * False - if a movie with the given id is not present in the database
     */
    public boolean moviePresent(String id) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (m: Movie) WHERE m.id = '%s' RETURN m".formatted(id);
            Result result = tx.run(query);
            Boolean isPresent = result.hasNext();
            tx.commit();
            return isPresent;
        }
    }

    /*
     * This method returns:
     * True - if there exists a relationship between the given movie and actor
     * False - if there is no relationship between the given movie and actor
     */
    public boolean hasRelationship(String movieId, String actorId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "RETURN EXISTS((:Actor{id:'%s'})-[:ACTED_IN]-(:Movie{id:'%s'}))".formatted(actorId, movieId);
            Result result = tx.run(query);
            Boolean relationship = result.next().values().get(0).asBoolean();
            tx.commit();
            return relationship;
        }
    }

    /*
     * This method returns:
     * 200 - There exists a relationship between the given movie and actor
     * 201 - There is no relationship between the given movie and actor
     * 404 - The given actor and/or movie do(es) not exist
     * 500 - Server error
     */
    public int relationship(String movieId, String actorId) {
        try {
            // return a 404 if the given movie or actor does not exist
            if(!moviePresent(movieId) || !actorPresent(actorId)) {
                return 404;
            }
            else {
                if(hasRelationship(movieId, actorId)) {
                    // return a 200 if there is a relationship between the given actor and movie
                    return 200;
                }
                else {
                    // return a 201 if there is no relationship between the given actor and movie
                    return 201;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return 500;
        }
    }

    /*
     * Returns the name of the actor whose actorId is provided
     */
    public String getActorAsAString(String actorId) {
        // if an actor with the given actorId DNE, return an empty string
        if(!actorPresent(actorId)) {
            return "";
        }
        else {
            try(Session session = driver.session()){
                Transaction tx = session.beginTransaction();
                String query = "MATCH (a: Actor) WHERE a.id = '%s' RETURN a.Name".formatted(actorId);
                Result result = tx.run(query);
                String name = result.next().values().get(0).asString();
                tx.commit();
                return name;
            }
            catch(Exception e) {
                e.printStackTrace();
                return "error";
            }
        }
    }

    /*
     * Given an actorId, returns the list of movies in which the actor ACTED_IN
     */
    public List<String> getActedInMovies(String actorId){
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (a {id: '%s'})-[:ACTED_IN]->(b) RETURN b".formatted(actorId);
            Result result = tx.run(query);
            List<String> movies = new ArrayList<>();
            while(result.hasNext()) {
                org.neo4j.driver.Record row = result.next();
                String movie = row.values().get(0).get("id").toString();
                movies.add(movie);
            }
            tx.commit();
            return movies;
        }
    }
    /*
    * Given an actorId, it computers the baconNumber for the actor associated to given Id.
    * This method handles all the database-side transactions to make the endpoint request send
    * This method returns:
    * 200 OK - For a successful computation
    * 400 BAD REQUEST - If the request body is improperly formatted or
                        missing required information
    * 500 INTERNAL SERVER ERROR - Java Exception thrown
    *
    * Edit 1: Modified to implement the correct getBaconNumber() method, which works correctly!
     */

    public int getBaconNumber(String actorId) {

        try(Session session = driver.session()){

            try(Transaction tx = session.beginTransaction()){

                // First we see whether our input query is Kevin Bacon or not, in which case we return 0
                if(actorId.equals("nm0000102")) {

                    session.close();
                    responseBody = new JSONObject().put("baconNumber", "0" );
                    return 200;
                }
                //Now we have to check whether an actor with the input actor even exists or not
                if(!actorPresent(actorId)) {
                    session.close();
                    return 400;
                }

                /* This is the part where we initiate the search process and calculating Bacon's Number
                Taken from Neo4j Docs - On Shortest Paths in Neo4j DB
                * Link: https://neo4j.com/docs/cypher-manual/current/clauses/match/#query-shortest-path */
                Result checkPath = tx.run(("MATCH (a:Actor {id: '%s'})," +
                        " (b:Actor {id: 'nm0000102'}), p = shortestPath((a)-[*..15]-(b)) RETURN p").formatted(actorId));

                // We have to see if a path exists or not using an if condition
                if(!checkPath.hasNext()) {
                    session.close();
                    return 404;
                }

                /* We get the `p` value from our CYPHER query, which is double of what we need
                   The `p` value is basically the nodes traversed in finding the shortest path.
                    We get the number of nodes traversed through the .get().size() method and then
                    divide by 2 to get the correct number of connections between input and Kevin Bacon
                    SOURCE: Neo4j Manual, extracted from code version of queries*/

                String baconNumber = String.valueOf((checkPath.next().get("p").size())/2);
                responseBody = new JSONObject().put("baconNumber", baconNumber);
                return 200;
            }

            // We also have to set up the internal server error status codes
            catch (Exception e){
                return 500;
            }
        }
        catch (Exception e){
            return 500;
        }
    }

    /*
     * Given an actor Id, this methods calculates the baconPath for the associated actor
     */
    public List<String> baconPath(String actorId){
        List<String> nodes = new ArrayList<String>();
        Session session = driver.session();
        Transaction tx = session.beginTransaction();
        String query = "MATCH(a:Actor{id:'%s'}), (b:Actor{id:'nm0000102'}), p = shortestPath((a)-[:ACTED_IN*]-(b)) RETURN nodes(p)".formatted(actorId);

        Result result = tx.run(query);
        if(!result.hasNext()) {
            // return movies as an empty array
            return nodes;
        }
        else {
            Value array = result.next().values().get(0);
            for(int i=0; i < array.size(); i++) {
                nodes.add(array.get(i).get("id").toString());
            }
            return nodes;
        }
    }

    // Removed redundant getResponse method in this corrected version as well :(
    public JSONObject getResponse() {
        return this.responseBody;
    }
}
