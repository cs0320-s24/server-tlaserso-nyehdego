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
    private final APICodeSource acsState;
    public BroadbandHandler(APICodeSource acsState) {

        this.acsState = acsState;
    }

    public Object handle(Request request, Response response){

        String county = request.queryParams("county");
        String state = request.queryParams("state");

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringobject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringobject);

        Map<String, Object> responseMap = new HashMap<>();

        if (state == null || county == null) {
            responseMap.put("type", "error");
            responseMap.put("error in type", "missing parameter");
            responseMap.put("error in args", state == null ? "state" : "county");
            return adapter.toJson(responseMap);
        }

        try {
            List<String> result = acsState.getBandWidth(county, state);
            responseMap.put("type", "success");
            responseMap.put("Bandwith", result);
            return adapter.toJson(responseMap);
        } catch (DataSourceException e) {
            responseMap.put("type", "bad_request");
            responseMap.put("error_type", "error_datasource");
            responseMap.put("parameters", e.getMessage());
            return adapter.toJson(responseMap);
        }
    }

}
