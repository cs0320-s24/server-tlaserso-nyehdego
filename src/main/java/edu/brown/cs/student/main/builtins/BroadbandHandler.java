package edu.brown.cs.student.main.builtins;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class BroadbandHandler implements Route {

    private final ACSDatasource acsState;
    public broadBandHandler(ACSDatasource acsState) {
        this.acsState = acsState;
    }

    public Object handle(Request request, Response response){

        String state = request.queryParams("state");
        String county = request.queryParams("county");
        DateTimeFormatter requestTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime requestLocalTime = LocalDateTime.now();

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringobject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> Adapter = moshi.adapter(mapStringobject);
        JsonAdapter<ACSData> ACSDataAdapter = moshi.adapter(ACSData.class);
        Map<String, Object> responseMap = new HashMap<>();

        try {
            ACSLocation acsLocation = new ACSLocation(state, county);
            ACSData data = acsState.findPercentage(acsLocation);
        }



        return request;
    }

}
