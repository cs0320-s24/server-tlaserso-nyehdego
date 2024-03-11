package edu.brown.cs.student.main.builtins.csv;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * view handler class handles all methods having to do with outputting  the csv for viewing
 */
public class ViewHandler implements Route {

    /**
     * handle gets the data and puts it into a repsosneMap that is returned to the Server
     * @param request
     * @param response
     * @return
     */
    public Object handle(Request request, Response response){
        ArrayList<ArrayList<String>> data = Server.getCSVData();
        if (data == null) {
            // could print somnething here maybe
            return new ViewFailureResponse("error").serialize();
        }
        Map<String, Object> viewResponseMap = new HashMap<>();
        viewResponseMap.put("data", data);
        return new ViewSuccessResponse(viewResponseMap).serialize();
    }

    /**
     * successful view repsonse object for handle method
     * @param response_type
     * @param responseMap
     */

    public record ViewSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public ViewSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<ViewHandler.ViewSuccessResponse> adapter = moshi.adapter(ViewHandler.ViewSuccessResponse.class);
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

    /** unsuccessful view repsonse object for handle method */
    public record ViewFailureResponse(String response_type) {
        public ViewFailureResponse() {
            this("error");
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(ViewHandler.ViewFailureResponse.class).toJson(this);
        }
    }
}
