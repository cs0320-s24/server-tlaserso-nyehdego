package edu.brown.cs.student.main.builtins;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Set;

public class BroadbandHandler implements Route {


    public Object handle(Request request, Response response){
        Set<String> params = request.queryParams();
        System.out.println(params);
        return request;
    }

}
