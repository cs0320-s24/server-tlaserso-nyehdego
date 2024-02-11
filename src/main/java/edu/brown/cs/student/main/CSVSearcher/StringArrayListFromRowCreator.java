package edu.brown.cs.student.main.CSVSearcher;

import edu.brown.cs.student.main.CreatorFromRow;
import edu.brown.cs.student.main.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/*
This is an example of a class that returns an arraylist of strings from the input data.  It is used by the searcher
class.
 */
public class StringArrayListFromRowCreator implements CreatorFromRow<ArrayList<String>> {
  private int malformed_tracker;

  public StringArrayListFromRowCreator() {
    this.malformed_tracker = 0;
  }

  public ArrayList<String> create(List<String> row) throws FactoryFailureException {
    ArrayList<String> list = new ArrayList<>(0);
    for (int i = 0; i < row.size(); i++) {
      list.add((row.get(i)));
    }
    return list;
  }
}
