package edu.brown.cs.student.main.builtins.broadband;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import okio.Buffer;

/**
 * This class is responsbile for querying into API or cache database to find the correct response
 * based on user input
 */
public class APICodeSource {
  public static List<List<String>> sCodes;

  /**
   * The constructor calls the list of states to their codes so that we don't have to re-query it.
   */
  public APICodeSource() {
    APICodeSource.stateCodesInitializer();
  }

  public List<List<String>> getCodes() {
    return APICodeSource.sCodes;
  }

  /** Method that synchronizes the query of the states. */
  private static synchronized void stateCodesInitializer() {
    if (APICodeSource.sCodes == null) {
      try {
        APICodeSource.getStateCodes();
      } catch (DataSourceException e) {
        // Handle the exception (e.g., log it) or set a default value for stateCodes
        APICodeSource.sCodes = new ArrayList<>();
      }
    }
  }
  /**
   * This method reads the JSON list of string containing state and code.
   *
   * @return
   * @throws DataSourceException
   */
  private static List<List<String>> getStateCodes() throws DataSourceException {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      // NOTE: important! pattern for handling the input stream
      APICodeSource.sCodes =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (APICodeSource.sCodes.isEmpty()) throw new DataSourceException("Malformed response");
      return APICodeSource.sCodes;
    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
  }

  /**
   * Calls an API that connects to the server we are requesting information from
   *
   * @param requestURL
   * @return
   * @throws DataSourceException
   * @throws IOException
   */
  private static HttpURLConnection connect(URL requestURL) throws DataSourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DataSourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200) {
      String responseMessage = clientConnection.getResponseMessage();
      if (responseMessage == null) {
        responseMessage = "Unknown error occurred";
      }
      throw new DataSourceException(
          "unexpected: API connection not success status " + responseMessage);
    }
    return clientConnection;
  }

  /**
   * Returns a JSON of the bandwidth, date and time by calling on the helper methods in the class
   *
   * @param county
   * @param state
   * @return
   * @throws DataSourceException
   */
  public static List<String> getBandWidth(String county, String state) throws DataSourceException {
    APICodeSource.stateCodesInitializer();
    try {
      String stateCode = APICodeSource.convertNameToStateCode(state);
      String countyCode = APICodeSource.countyCode(APICodeSource.subsetData(stateCode), county);
      LocalDate date = LocalDate.now();
      LocalTime time = LocalTime.now();
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode);
      List<List<String>> data = APICodeSource.getDataFromURL(requestURL);

      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<String> result = new ArrayList<>();
      result.add("Band Width:" + data.get(1).get(1));
      result.add("State:" + state);
      result.add("County:" + county);
      result.add("Date:" + date);
      result.add("Current time:" + time);

      if (result.isEmpty()) throw new DataSourceException("Malformed response from ACS");
      return new ArrayList<>(result);

    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
  }

  /**
   * This helper returns a list of all the counties for a state, used stack overflow for help
   *
   * @param requestURL
   * @return
   * @throws DataSourceException
   * @throws IOException
   */
  public static List<List<String>> getDataFromURL(URL requestURL)
      throws DataSourceException, IOException {
    List<List<String>> data = new ArrayList<>();
    try {
      // Make an HTTP GET request to the URL
      String url = requestURL.toString(); // Replace with your URL
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder jsonResponse = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        jsonResponse.append(line);
      }
      reader.close();

      // Parse JSON using Gson
      Gson gson = new Gson();
      TypeToken<List<List<String>>> token = new TypeToken<List<List<String>>>() {};
      data = gson.fromJson(jsonResponse.toString(), token.getType());

    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  /**
   * This method takes the stateName to search through the saved state data to find the right
   * stateCode.
   *
   * @param stateName
   * @return
   * @throws DataSourceException
   */
  public static String convertNameToStateCode(String stateName) throws DataSourceException {
    String stateCode = "*";
    for (int i = 0; i < APICodeSource.sCodes.size(); i++) {
      if (APICodeSource.sCodes.get(i).get(0).equalsIgnoreCase(stateName)) {
        stateCode = APICodeSource.sCodes.get(i).get(1);
        break;
      }
    }
    if (!Objects.equals(stateCode, "*")) {
      return stateCode;
    } else {
      throw new DataSourceException("State not found");
    }
  }

  /**
   * This is a subset of the data in ACS of all counties from the same state
   *
   * @param stateCode
   * @return
   * @throws DataSourceException
   */
  public static List<List<String>> subsetData(String stateCode) throws DataSourceException {
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"
                  + stateCode);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      // NOTE: important! pattern for handling the input stream
      List<List<String>> countyList =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (countyList.isEmpty()) {
        throw new DataSourceException("Malformed response from NWS");
      }
      return countyList;
    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
  }

  /**
   * This method loops through the list of counties and states to find its county code
   *
   * @param countyName
   * @return
   */
  public static String countyCode(List<List<String>> counties, String countyName)
      throws DataSourceException {
    String stateCode = "*";
    for (int i = 0; i < counties.size(); i++) {
      String name = counties.get(i).get(0);
      List<String> parts = Arrays.asList(name.split(","));
      if (parts.get(0).equalsIgnoreCase(countyName)) {
        stateCode = counties.get(i).get(3);
      }
    }
    if (stateCode != "*") {
      return stateCode;
    } else {
      throw new DataSourceException("County not found");
    }
  }
}
