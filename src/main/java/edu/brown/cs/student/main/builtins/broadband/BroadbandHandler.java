package edu.brown.cs.student.main.builtins.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Broadber handler si a class that manages all commands within the broadband query
 */
public class BroadbandHandler implements Route {
    private APIDataCache cache;

    /**
     * constructor builds a cache object to use for the data found from broadband
     */
    public BroadbandHandler(){
        this.cache = new APIDataCache(10, 10);
    }

    /**
     * handle method takes a request for a given state and county and stores it into cache if not already there
     * @param request
     * @param response
     * @return
     */
    public Object handle(Request request, Response response){


        String county = request.queryParams("county");
        String state = request.queryParams("state");

        Map<String, Object> responseMap = new HashMap<>();

        if (state == null || county == null) {
            responseMap.put("error in type", "missing parameter");
            responseMap.put("error in args", state == null ? "state" : "county");
            return new BroadbandFailureResponse(responseMap);
        }

        String statecounty = county + "." + state;

        try {
            List<String> result = this.cache.get(statecounty);
            responseMap.put("Bandwidth", result);
            return new BroadbandSuccessResponse(responseMap);
        } catch (ExecutionException e) {
            responseMap.put("error_type", "error_datasource");
            responseMap.put("parameters", e.getMessage());
            return new BroadbandFailureResponse(responseMap);
        }
    }

    /**
     * a response handler for when Brodband suceeds
     * @param response_type
     * @param responseMap
     */
    public record BroadbandSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public BroadbandSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<BroadbandHandler.BroadbandSuccessResponse> adapter = moshi.adapter(BroadbandHandler.BroadbandSuccessResponse.class);
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

    /** Response object to send if someones request failed, with a message
     * */
    public record BroadbandFailureResponse(String response_type, Map<String, Object> responseMap) {
        public BroadbandFailureResponse(Map<String, Object> responseMap) {
            this("error", responseMap);
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class).toJson(this);
        }
    }

}
