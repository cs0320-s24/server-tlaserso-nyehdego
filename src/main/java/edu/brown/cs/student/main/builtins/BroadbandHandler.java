package edu.brown.cs.student.main.builtins;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadbandHandler implements Route {
    public Object handle(Request request, Response response){
        APICodeSource acsState = new APICodeSource();

        String county = request.queryParams("county");
        String state = request.queryParams("state");

        Map<String, Object> responseMap = new HashMap<>();

        if (state == null || county == null) {
            responseMap.put("error in type", "missing parameter");
            responseMap.put("error in args", state == null ? "state" : "county");
            return new BroadbandFailureResponse(responseMap);
        }

        try {
            List<String> result = acsState.getBandWidth(county, state);
            responseMap.put("Bandwith", result);
            return new BroadbandSuccessResponse(responseMap);
        } catch (DataSourceException e) {
            responseMap.put("error_type", "error_datasource");
            responseMap.put("parameters", e.getMessage());
            return new BroadbandFailureResponse(responseMap);
        }
    }
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

    /** Response object to send if someone requested soup from an empty Menu */
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
