package edu.brown.cs.student;

import edu.brown.cs.student.main.CSVSearcher.Searcher;
import java.io.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearcherTest {
  /*
  This test tests the basic functionality of the searcher by verifying that it finds the search term the correct number of times
  for the case were the wrong column is searched, correct column is searched, no column is specified, and a bad term is searched.
   */
  @Test
  public void basicsearchTest() {
    try {
      Searcher glorifiedPrinter =
          new Searcher(
              "C:\\Users\\Tommy\\Documents\\Cs320\\csv-tlasersohn\\data\\census\\income_by_race.csv");
      Assertions.assertEquals(glorifiedPrinter.search("2020", true, "0", true), 0);
      Assertions.assertEquals(glorifiedPrinter.search("2020", true, "year", false), 20);
      Assertions.assertEquals(glorifiedPrinter.search("2020", true, null, false), 20);
      Assertions.assertEquals(glorifiedPrinter.search("2022", true, "year", false), 0);
    } catch (IOException e) {
      System.err.println("Someone messed with the data folder");
    }
  }
  /*
  This test is run on a malformed file, showing how a bad input could result in a search term not being found if a column
  is specified but found if no column is specified.  This is partially a problem with the provided regex.
   */
  @Test
  public void malformedSearchTest() {
    try {
      Searcher glorifiedPrinter =
          new Searcher(
              "C:\\Users\\Tommy\\Documents\\Cs320\\csv-tlasersohn\\data\\malformed\\malformed_signs.csv");
      Assertions.assertEquals(glorifiedPrinter.search("nick", false, null, false), 1);
      Assertions.assertEquals(glorifiedPrinter.search("nick", false, "2", true), 0);
    } catch (IOException e) {
      System.err.println("Someone messed with the data folder");
    }
  }
  /*
  This verifies that entering a bad path results an error.  The protection from entering a path outside the correct
  directory is in the main class.
   */
  @Test
  public void badFilePathTest() {
    Assertions.assertThrows(
        IOException.class,
        () -> {
          Searcher madeToFail = new Searcher("This is a bad path");
        });
  }
}
