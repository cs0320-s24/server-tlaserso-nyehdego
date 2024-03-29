package edu.brown.cs.student.main;

import static spark.Spark.after;

import edu.brown.cs.student.main.builtins.broadband.BroadbandHandler;
import edu.brown.cs.student.main.builtins.csv.SearchHandler;
import edu.brown.cs.student.main.builtins.csv.ViewHandler;
import edu.brown.cs.student.main.builtins.csv.loadHandler;
import java.util.ArrayList;
import spark.Spark;

/** class responsible for launching the server and routing based on the desired query class */
public class Server {

  private static ArrayList<ArrayList<String>> data;

  public static ArrayList<ArrayList<String>> getCSVData() {
    return Server.data;
  }

  public static void setCSVData(ArrayList<ArrayList<String>> csvData) {
    Server.data = csvData;
  }

  /**
   * main sets up the server on a port and looks for keywords of the handlers
   *
   * @param args
   */
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Sets up data needed for the OrderHandler. You will likely not read from local
    // JSON in this sprint.
    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("load", new loadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadbandHandler());
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
