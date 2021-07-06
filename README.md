# The-Six-Degrees-of-Kevin-Bacon

### Description

This is the implementation of the backend for a service that computes the [Six degrees of
Kevin Bacon](https://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon). This problem can be restated as finding the shortest path between Kevin Bacon
and a given actor (via shared movies). THis has been achieved using a specific dependency injection framework (Google Dagger2) and Neo4j as the database management system

### Objective 

- Explore NoSQL/Graph Databases(Neo4j)
- To create REST API endpoints that are supported by Neo4j Graph Databases
- To utilize Git, Gitflow, and Code Style correctly
- Using enterprise design patterns, specifically dependency injection using Google Dagger2
- Learning and applying CI/CD

### Completed Features

- Server is up and running
- All GET and PUT requests work as expected (tested through POSTMAN)

### Caveats

- The format of the list of movies received by `getActor` request, and the bacon path generated by `computeBaconPath` request have an extra `/` in them 

Example input - Testing a `computeBaconPath` request

![sample_input](https://user-images.githubusercontent.com/56613320/124346055-a7bc7080-dbaa-11eb-84be-3424720cdd71.PNG)



Actual output received for `computeBaconPath` (as of now)

```json
{
    "baconPath": [
        "\"na0000101\"",
        "\"nm003\"",
        "\"na0000103\"",
        "\"nm002\"",
        "\"na0000104\"",
        "\"nm001\"",
        "\"nm0000102\""
    ]
}

```

Expected output for `computeBaconPath`(Notice how only the formatting of the baconPath is off)

```json

{
    "baconPath": [
        "na0000101",
        "nm003",
        "na0000103",
        "nm002",
        "na0000104",
        "nm001",
        "nm0000102"
    ]
}
```


### TODO
- Complete `AppTest.java` under `src/test/java/ca/utoronto/utm/mcs`
- Fix the format in the which bacon path is displayed.
