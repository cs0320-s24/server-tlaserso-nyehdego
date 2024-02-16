package edu.brown.cs.student.main.builtins;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import okio.Buffer;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static spark.Spark.connect;

public class APICodeSource {

    private List<List<String>> sCodes;

    public List<List<String>> findStateCodes() {
        try {
            URL URLrequest = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
            HttpURLConnection clientsConnection = connectionErrorHandler(URLrequest);
            Moshi moshiBuild = new Moshi.Builder().build();
            JsonAdapter<List> jsonAdapter = moshiBuild.adapter(List.class).nonNull();
            this.sCodes = jsonAdapter.fromJson(new Buffer().readFrom(clientsConnection.getInputStream()));
            clientsConnection.disconnect();
            if (sCodes == null) {
                throw new DataSourceException("error; state code query is null");
            }
            return this.sCodes;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DataSourceException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpURLConnection connectionErrorHandler(URL requestURL) throws IOException, DataSourceException {
        URLConnection urlConnection = requestURL.openConnection();
        if (!(urlConnection instanceof HttpURLConnection)) {
            throw new DataSourceException("unexpected: result of connection wasn't HTTP");
        }

        HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
        clientConnection.connect(); // GET
        if (clientConnection.getResponseCode() != 200) {
            String responseMessage = clientConnection.getResponseMessage();
            if (responseMessage == null) {
                responseMessage = "Unknown error occured";
            }
            throw new DataSourceException("unexpected: API connection not success status " + responseMessage);
        }
        return clientConnection;
    }

    public List<String> findBandwith(String county, String state) throws DataSourceException {
        try {
            String stateC = this.nameToStateCode(state);
            String countyC = this.countyFinder(this.countyData(stateC), county);
            LocalTime time = LocalTime.now();
            LocalDate date = LocalDate.now();

            URL URLrequest = new URL("https", "api.census.gov", "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"+countyC+"&in=state:"+stateC);
            List<List<String>> bandwithLine = this.parseBandwith(URLrequest);

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List> jsonAdapter = moshi.adapter(List.class).nonNull();
            List<String> query = new ArrayList<>();
            query.add("Bandwidth:" + bandwithLine.get(1).get(1));
            query.add("State:" + state);
            query.add("County:" + county);
            query.add("Date:" + date);
            query.add("Time:" + time);

            if (query.isEmpty()) {
                throw new DataSourceException("Bad response from Census query");
            }
            return new ArrayList<>(query);
        } catch(IOException e) {
            throw new DataSourceException(e.getMessage());
        } catch (DataSourceException e) {
            throw new RuntimeException(e);
        }
    }

    // stack overflow:
    public List<List<String>> parseBandwith(URL requestURL) throws DataSourceException, IOException {
        List<List<String>> countryStateLine = new ArrayList<>();
        try {
            // Make an HTTP GET request to the URL
            String urlRequest = requestURL.toString(); // Replace with your URL
            HttpURLConnection connections = (HttpURLConnection) new URL(urlRequest).openConnection();
            connections.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connections.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();

            // Parse JSON using Gson
            Gson gson = new Gson();
            TypeToken<List<List<String>>> token = new TypeToken<List<List<String>>>() {};
            countryStateLine = gson.fromJson(jsonResponse.toString(), token.getType());

            // Now 'data' contains your JSON data as a List of List of String

        } catch (Exception e) {
            e.printStackTrace();
        }
        return countryStateLine;
    }

    public List<List<String>> countyData(String stateCode) throws DataSourceException {
        try {
            URL requestURL = new URL("https", "api.census.gov", "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"+stateCode);
            HttpURLConnection clientConnection = connectionErrorHandler(requestURL);
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
            // NOTE: important! pattern for handling the input stream
            List<List<String>> countyList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();
            if(countyList.isEmpty()){
                throw new DataSourceException("Malformed response from NWS");}
            return countyList;
        } catch(IOException | DataSourceException e) {
            throw new DataSourceException(e.getMessage());
        }
    }

    public String nameToStateCode(String stateName) throws DataSourceException {
        String sCode = "*";
        for (int i = 0; i < this.sCodes.size(); i++) {
            if (this.sCodes.get(i).get(0).equals(stateName)) {
                sCode = this.sCodes.get(i).get(1);
            }
        }
        if (sCode != "*") {
            return sCode;
        } else {
            throw new DataSourceException("State not found");
        }

    }

    public String countyFinder(List<List<String>> counties, String countyName) throws DataSourceException {
        String cCode = "*";
        for (int i = 0; i < counties.size(); i+=1) {
            String name = counties.get(i).get(0);
            List<String> split = Arrays.asList(name.split(","));
            if (split.get(0).equals(countyName)) {
                cCode = counties.get(i).get(3);
            }
        }
        if (cCode != "*") {
            return cCode;
        } else {
            throw new DataSourceException("error: county could not be found");
        }
    }
}
