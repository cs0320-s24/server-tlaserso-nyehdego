package edu.brown.cs.student.main;

import static java.lang.Integer.parseInt;

import edu.brown.cs.student.main.CSVSearcher.Searcher;
import java.io.*;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {}
  /*
  This method sets up the basic running through user prompts and then calls the more advanced run settings.
   */
  private void run() {
    Boolean badAnswer = true;
    String YorN;
    Scanner inputParser = new Scanner(System.in);
    while (badAnswer) {
      System.out.println("Would you like to run the program?  Please answer only Y or N");
      YorN = inputParser.nextLine();
      if (YorN.equalsIgnoreCase("Y")) {
        this.runner(inputParser);
      } else if (YorN.equalsIgnoreCase("N")) {
        System.out.println("exiting program.");
        inputParser.close();
        System.exit(0);
      } else {
        System.out.println("please only answer Y or N");
      }
    }
  }
  /*
  this method continues to prompt user inputs to plug into the searcher method in order to search.
   */

  public void runner(Scanner inputParser) {
    String filename = "";
    String toFind;
    Boolean hasHeaders = false;
    String ColumnID = null;
    Boolean idIsInt = true;
    Boolean badAnswerOG = true;
    while (badAnswerOG) {
      filename = "C:\\Users\\Tommy\\Documents\\Cs320\\csv-tlasersohn\\data\\";
      System.out.println("Please enter relative filepath in data directory");
      filename += inputParser.nextLine();
      try {
        Reader test = new FileReader(filename);
        test.close();
        badAnswerOG = false;
        System.out.println("Filepath accepted.");
      } catch (FileNotFoundException e) {
        System.out.println(
            "Sorry, this was not a legitimate path.  If you believe this is an error, please try again.  Or, enter new path.");
        badAnswerOG = true;
      } catch (IOException e) {
        System.err.println("error closing test reader.");
        System.exit(1);
      }
    }
    System.out.println("Please enter search term (search is not case sensitive)");
    toFind = inputParser.nextLine();
    String YorN;
    Boolean badAnswer = true;
    while (badAnswer) {
      System.out.println("Does your CSV have headers?  Please answer only Y or N");
      YorN = inputParser.nextLine();
      if (YorN.equalsIgnoreCase("Y")) {
        hasHeaders = true;
        badAnswer = false;
      } else if (YorN.equalsIgnoreCase("N")) {
        hasHeaders = false;
        badAnswer = false;
      } else {
        System.out.println("please only answer Y or N");
      }
    }

    badAnswer = true;
    while (badAnswer) {
      System.out.println(
          "would you like to specify a column to search in?  Please only enter Y or N");
      YorN = inputParser.nextLine();
      if (YorN.equalsIgnoreCase("Y")) {
        badAnswer = false;
        if (hasHeaders) {
          System.out.println("please enter either a header name or column index");
          ColumnID = inputParser.nextLine();
          try {
            parseInt(ColumnID);
            idIsInt = true;
          } catch (NumberFormatException e) {
            idIsInt = false;
          }
        } else {
          Boolean badanswer2 = true;
          while (badanswer2) {
            System.out.println("Please enter an integer value");
            ColumnID = inputParser.nextLine();
            try {
              parseInt(ColumnID);
              idIsInt = true;
              badanswer2 = false;
            } catch (NumberFormatException e) {
              System.out.println("please only enter integer values.");
            }
          }
        }
      } else if (YorN.equalsIgnoreCase("N")) {
        ColumnID = null;
        badAnswer = false;
      } else {
        System.out.println("please only answer Y or N");
      }
    }
    System.out.println("Thank you.  Searching now.");
    try {
      Searcher glorifiedPrinter = new Searcher(filename);
      glorifiedPrinter.search(toFind, hasHeaders, ColumnID, idIsInt);
    } catch (IOException e) {
      System.err.println("Sorry, file not found");
    }
  }
}
