package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

// Extra imported libraries for testing
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// TODO Please Write Your Tests For CI/CD In This Class. You will see these tests pass/fail on github under github actions.

public class AppTest {

    /* Testing for the `addActor` PUT request through a 200 status check and a
     4XX error response check */

    // Checking the 200 status check first
    @Test
    public void addActorSuccess() throws JSONException, IOException {

        // Step 1: Create the request body for the PUT request;
        JSONObject requestBody = new JSONObject();

        // Populating the created JSONObject with values, which is required input for `addActor` request
        requestBody.put("name", "Roger Federer");
        requestBody.put("actorId", "3000");

        // Now we have to make a connection, which does the work of writing the request body and method
        URL url = new URL("http://localhost:8080/api/v1/addActor");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        outputStreamWriter.write(requestBody.toString());
        outputStreamWriter.close();
        outputStream.close();
        connection.connect();

        // Getting the response status which is generated through the connection above and our code
        int responseStatus = connection.getResponseCode();

        //Asserting whether the value we get is equal to the value that is expected, i.e., 200
        assertEquals(200, responseStatus);


    }
  }
