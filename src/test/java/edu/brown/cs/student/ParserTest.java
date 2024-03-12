package edu.brown.cs.student;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVSearcher.StringArrayListFromRowCreator;
import java.io.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserTest {

  /*
  This test asserts that the headers are saved in the 0 row, and that parsing using the designed arraylist from row creator
  indeed saves all the entries in the row strings.
   */
  @Test
  public void basicParseTest() {
    try {
      Reader CSVReader =
          new FileReader(
              "C:\\Users\\Tommy\\Documents\\Cs320\\csv-tlasersohn\\data\\census\\income_by_race.csv");
      CSVParser<ArrayList<String>> parser =
          new CSVParser<>(CSVReader, new StringArrayListFromRowCreator());
      try {
        ArrayList<ArrayList<String>> DataSet = parser.parse();
        Assertions.assertEquals(DataSet.get(1).get(4), "85413");
        Assertions.assertEquals(DataSet.get(21).get(1), "Asian");
        Assertions.assertEquals(DataSet.get(0).get(0), "ID Race");
      } catch (IOException e) {
        System.out.println("Parser failed");
      }
    } catch (FileNotFoundException e) {
      System.out.println("lol you deleted the data folder");
    }
  }
  /*
  This test shows that entering in malformed data files leads to a final product with varying columns per row.  This is
  partially a problem with the provided regex, as it reduces empty values in the csv to null values and therefore makes
  the output list, and therefore in this test the output row, of inconsistent size.
   */
  @Test
  public void MalformedParseTest() {
    try {
      Reader CSVReader =
          new FileReader(
              "C:\\Users\\Tommy\\Documents\\Cs320\\csv-tlasersohn\\data\\malformed\\malformed_signs.csv");
      CSVParser<ArrayList<String>> parser =
          new CSVParser<>(CSVReader, new StringArrayListFromRowCreator());
      try {
        ArrayList<ArrayList<String>> DataSet = parser.parse();
        Assertions.assertEquals(DataSet.get(7).size(), 1);
        Assertions.assertEquals(DataSet.get(3).size(), 3);
      } catch (IOException e) {
        System.out.println("Parser failed");
      }
    } catch (FileNotFoundException e) {
      System.out.println("lol you deleted the data folder");
    }
  }
  /*
  This test functions to make sure that the parser works with a different creator from row, and that it correctly throws an
  exception if a file with headers is used, as the headers are incompatible with being converted into the object.
   */
  //  @Test
  //  public void AlternateConstructorParseTest() {
  //    try {
  //      Reader CSVReader =
  //          new FileReader(
  //
  // "C:\\Users\\Tommy\\Documents\\Cs320\\csv-tlasersohn\\data\\census\\income_by_race.csv");
  //      CSVParser<IncomeFromRaceClass> parser =
  //          new CSVParser<>(CSVReader, new IncomeClassCreatorfromRow());
  //      try {
  //        ArrayList<IncomeFromRaceClass> DataSet = parser.parse();
  //        Assertions.assertThrows(
  //            IOException.class,
  //            () -> {
  //              parser.parse();
  //            });
  //        Assertions.assertEquals(DataSet.get(1).income, "85413");
  //        Assertions.assertEquals(DataSet.get(21).race, "asian");
  //      } catch (IOException e) {
  //        System.out.println("Parser failed");
  //      }
  //    } catch (FileNotFoundException e) {
  //      System.out.println("lol you deleted the data folder");
  //    }
  //  }
  /*
  This test makes sure that the parser works with other types of readers, in this case a string reader.
   */
  @Test
  public void StringReaderParseTest() {

    Reader CSVReader = new StringReader("test1, test2, test3");
    CSVParser<ArrayList<String>> parser =
        new CSVParser<>(CSVReader, new StringArrayListFromRowCreator());
    try {
      ArrayList<ArrayList<String>> DataSet = parser.parse();
      Assertions.assertEquals(DataSet.get(0).get(0), "test1");
      Assertions.assertEquals(DataSet.get(0).get(1), "test2");
      Assertions.assertEquals(DataSet.get(0).get(2), "test3");
    } catch (IOException e) {
      System.out.println("Parser failed");
    }
  }
}
