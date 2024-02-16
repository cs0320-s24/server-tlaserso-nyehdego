package edu.brown.cs.student.main.builtins;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVSearcher.Searcher;
import edu.brown.cs.student.main.Server;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class SearchHandler implements Route {

    public Object handle(Request request, Response response){
        QueryParamsMap qm = request.queryMap();
        String query = qm.value("query");
        String index = qm.value("index");
        ArrayList<ArrayList<String>> data = Server.getCSVData();

        if (data == null) {
            return new SearchFailureResponse("error: no data to view");

        }

        if (query == null) {
            return new SearchFailureResponse("error: bad request");
        }
        try{
            ArrayList<ArrayList<String>> searchResult = new Searcher(data).search(query, true,null, false);
            Map<String, Object> SearchResponseMap = new HashMap<>();
            SearchResponseMap.put("data", searchResult);
            return new SearchSuccessResponse(SearchResponseMap);
        } catch (IOException e){
            return new SearchFailureResponse("Error: bad input");
        }
    }
    public record SearchSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public SearchSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<SearchHandler.SearchSuccessResponse> adapter = moshi.adapter(SearchHandler.SearchSuccessResponse.class);
                return adapter.toJson(this);
            } catch (Exception e) {
                // For debugging purposes, show in the console _why_ this fails
                // Otherwise we'll just get an error 500 from the API in integration
                // testing.
                e.printStackTrace();
                throw e;
            }
        }
    }

    /** Response object to send if someone requested soup from an empty Menu */
    public record SearchFailureResponse(String response_type) {
        public SearchFailureResponse() {
            this("error");
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(SearchHandler.SearchFailureResponse.class).toJson(this);
        }
    }
}
