package edu.brown.cs.student.main.CSVParser;

import edu.brown.cs.student.main.CreatorFromRow;
import edu.brown.cs.student.main.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CSVParser<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private BufferedReader lineReader;
  private CreatorFromRow<T> dataObjectCreator;

  /*
  This is the constructor for the CSVParser class.  It takes in a reader object and a CreatorFromRow<T> object
  in order to read from the file specified from the reader.  It instantiates the parsedSet instance variable using
  the parse helper method.  It then closes the reader.
   */
  public CSVParser(Reader readFromFile, CreatorFromRow<T> dataObjectCreator) {
    this.lineReader = new BufferedReader(readFromFile);
    this.dataObjectCreator = dataObjectCreator;
  }

  /*
  This is a helper method for the constructor.  It uses the instance variable BufferedReader and CreatorFromRow.
  It uses the buffered reader to traverse through the file line by line, turning each line into a list of distinct strings and then
  using the input CreaterFromRow to turn the input into whatever object of type T the end user desires (in the example
  provided in the Searcher package, the StringArrayListFromRowCreator turns the list of strings into an arraylist of strings).
  This method returns an ArrayList of type T that contains the processed rows from the csv.
   */
  public ArrayList<T> parse() throws IOException {
    ArrayList<T> parsedList = new ArrayList<>(0);
    String line = "";
    try {
      while ((line = this.lineReader.readLine()) != null) {
        String[] result = this.regexSplitCSVRow.split(line);
        for (int j = 0; j < result.length; j++) {
          result[j] = this.postprocess(result[j]);
        }
        try {
          parsedList.add(this.dataObjectCreator.create(List.of(result)));
        } catch (FactoryFailureException f) {
          System.err.println("Sorry, input CreatorFromRow to turn row into object failed");
          throw new IOException("Input Creator from row failed");
        }
      }
    } catch (IOException e) {
      System.err.println("Sorry, an input/output error occurred with the input reader.");
      throw e;
    }
    try {
      lineReader.close();
    } catch (IOException e) {
      System.err.println("Error closing buffered reader.");
      throw e;
    }
    return parsedList;
  }

  /*
  This method is used to postprocess a string split using the given regex into the correct format.  It takes in a String
  as an argument and then returns the processed string.
   */
  private static String postprocess(String arg) {
    return arg
        // Remove extra spaces at beginning and end of the line
        .trim()
        // Remove a beginning quote, if present
        .replaceAll("^\"", "")
        // Remove an ending quote, if present
        .replaceAll("\"$", "")
        // Replace double-double-quotes with double-quotes
        .replaceAll("\"\"", "\"");
  }
}
