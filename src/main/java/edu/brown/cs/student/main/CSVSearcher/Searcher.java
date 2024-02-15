package edu.brown.cs.student.main.CSVSearcher;

import static java.lang.Integer.parseInt;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;

public class Searcher {
  private ArrayList<ArrayList<String>> DataSet;

  private ArrayList<ArrayList<String>> searchResult;

  /*
  This is the constructor for searcher.  It takes in a String filename and sets the dataset instance variable as
  the result of parsing.
   */
  public Searcher(ArrayList<ArrayList<String>> data) throws IOException {
    this.DataSet=data;
  }
  /*
  This is the search method which is the primary search method.  It takes a String toFind, which is the string searched for in the CSV,
  the boolean parameter hasHeaders, the String columnID, and the boolean IDisInt.  If no columnID is specified, the method searches through every entry using
  row, column form, and if it finds the searched string it prints the full row then immediately skips to the next row.
  If a columnID is specified as an int (indicated using the boolean and then verified by parsing as int) only the entries
  in that column are checked against the searched string.  If the columnID is specified as not an int and there are headers,
  the first row is searched to determine which column index has that header, and then the entries in that column are searched
  through.  This method prints the rows found in, a summary, and returns as an int the number of times the term was found.
   */
  public ArrayList<ArrayList<String>> search(String toFind, Boolean hasHeaders, String columnID, Boolean IDisInt) {
    int tracker = 0;
    int rowFoundIn;
    ArrayList<ArrayList<String>> result = new ArrayList<>();
    LinkedList<Integer> rowsIn = new LinkedList();
    if (columnID == null) {
      for (int i = 0; i < this.DataSet.size(); i++) {
        ArrayList<String> rowSet = this.DataSet.get(i);
        for (int j = 0; j < rowSet.size(); j++) {
          if (rowSet.get(j).toUpperCase().contains(toFind.toUpperCase())) {
//            System.out.println(rowSet);
            result.add(rowSet);
            rowFoundIn = i++;
            rowsIn.add(rowFoundIn);
            tracker++;
            break;
          }
        }
      }
    } else {
      int columnToSearch = 0;
      if (IDisInt) {
        try {
          columnToSearch = parseInt(columnID);
        } catch (NumberFormatException e) {
          System.err.println("Input ColumnID not an int");
          System.exit(1);
        }
      } else if (hasHeaders) {
        if (this.DataSet.size() > 0) {
          ArrayList<String> headerRow = this.DataSet.get((0));
          for (int i = 0; i < headerRow.size(); i++) {
            if (columnID.equalsIgnoreCase(headerRow.get(i))) {
              columnToSearch = i;
              break;
            }
            if (i == headerRow.size() - 1) {
              System.out.println("Header not found in file.  Searching in first column");
              columnToSearch = 1;
            }
          }
        }
      } else {
        System.err.println("If columnID is a string, CSV must have headers");
        System.exit(1);
      }
      for (int i = 0; i < this.DataSet.size(); i++) {
        ArrayList<String> rowSet = this.DataSet.get(i);
        if (columnToSearch >= rowSet.size()) {
          System.out.println(
              "Error: Column index is out of bounds of input file. It is possible that this is caused by"
                  + " a malformed input file containing empty rows.");
          System.out.println(
              "If you believe this may be the case, please try the same search without specifying a "
                  + "column to search in.");
          break;
        }
        if (rowSet.get(columnToSearch).toUpperCase().contains(toFind.toUpperCase())) {
//          System.out.println(rowSet);
          result.add(rowSet);
          rowFoundIn = i++;
          rowsIn.add(rowFoundIn);
          tracker++;
        }
      }
    }
    if (tracker == 0) {
      System.out.println("Sorry, failed to find searched term in file");
    } else {
      System.out.println("Summary:");
      System.out.println("Search term was found " + tracker + " times.");
      System.out.println("Search term was found in the following rows:");
      System.out.println(rowsIn);
    }
    return result;
  }
}
