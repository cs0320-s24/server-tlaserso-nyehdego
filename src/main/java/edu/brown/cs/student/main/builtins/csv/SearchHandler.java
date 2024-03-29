package edu.brown.cs.student.main.builtins.csv;

import static java.lang.Integer.parseInt;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVSearcher.Searcher;
import edu.brown.cs.student.main.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

  /**
   * This handles the searching builtin. It first parses through the input parameters before running
   * search.
   *
   * @param request
   * @param response
   * @return
   */
  public Object handle(Request request, Response response) {
    QueryParamsMap qm = request.queryMap();
    String query = qm.value("query");
    String index = qm.value("index");
    String hasHeadersString = qm.value("hasHeaders");
    ArrayList<ArrayList<String>> data = Server.getCSVData();
    Boolean hasHeaders = false;
    Boolean idIsInt = false;

    if (data == null) {
      return new SearchFailureResponse("error: no data to view");
    }

    if (query == null) {
      return new SearchFailureResponse("error: bad request");
    }
    if (hasHeadersString == null) {
      return new SearchFailureResponse("error: hasHeaders must be either Y or N");
    }
    if (hasHeadersString.equalsIgnoreCase("Y")) {
      hasHeaders = true;
    } else if (hasHeadersString.equalsIgnoreCase("N")) {
      hasHeaders = false;
    } else {
      return new SearchFailureResponse("error: hasHeaders must be either Y or N");
    }
    if (index != null) {
      if (hasHeaders) {
        try {
          parseInt(index);
          idIsInt = true;
        } catch (NumberFormatException e) {
          idIsInt = false;
        }
      } else {
        try {
          parseInt(index);
          idIsInt = true;

        } catch (NumberFormatException e) {
          return new SearchFailureResponse("error: index must be a number of there are no headers");
        }
      }
    }

    try {
      ArrayList<ArrayList<String>> searchResult =
          new Searcher(data).search(query, hasHeaders, index, idIsInt);
      Map<String, Object> SearchResponseMap = new HashMap<>();
      SearchResponseMap.put("data", searchResult);
      return new SearchSuccessResponse(SearchResponseMap).serialize();
    } catch (IOException e) {
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
        JsonAdapter<SearchHandler.SearchSuccessResponse> adapter =
            moshi.adapter(SearchHandler.SearchSuccessResponse.class);
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
