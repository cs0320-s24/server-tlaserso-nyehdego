package edu.brown.cs.student.main.builtins;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVSearcher.StringArrayListFromRowCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class loadHandler implements Route {

    public Object handle(Request request, Response response){
        Set<String> params = request.queryParams();
        System.out.println(params);
        String path = "data/";
        path += request.queryParams("path");
        Map<String, Object> responseMap = new HashMap<>();
        try{
            CSVParser<ArrayList<String>> parser = new CSVParser<ArrayList<String>>(new FileReader(path),new StringArrayListFromRowCreator());
            ArrayList<ArrayList<String>> output = parser.parse();
        }catch (FileNotFoundException e){
            System.err.println("File not found");
        }catch(IOException I){
            System.err.println(
                    "error occurred while parsing CSV file.  Please see previous error message for more details");
        }

    }


    public record LoadSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public LoadSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
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
    public record LoadFailureResponse(String response_type) {
        public LoadFailureResponse() {
            this("error");
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(LoadFailureResponse.class).toJson(this);
        }
    }
}
