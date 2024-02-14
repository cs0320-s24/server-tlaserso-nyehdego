package edu.brown.cs.student.main.builtins;

import edu.brown.cs.student.main.CSVSearcher.Searcher;
import edu.brown.cs.student.main.Server;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.ArrayList;

public class SearchHandler implements Route {

    public Object handle(Request request, Response response){
        QueryParamsMap qm = request.queryMap();
        String query = qm.value("query");
        String index = qm.value("index");
        String name = qm.value("name");

        ArrayList<ArrayList<String>> data = Server.getCSVData();

        if (data == null) {
            return new ErrorResponse("error: no data to view");

        }

        if (query == null) {
            return new ErroResppnse("error: bad request");
        }

        if (index != null) {
            if (name != null) {
                // cannot hae both an index and a column to look for
                return new ErroResppnse("error: bad request");
            }
            else {
                try {
                    ArrayList<ArrayList<String>> searchResult = new Searcher();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
