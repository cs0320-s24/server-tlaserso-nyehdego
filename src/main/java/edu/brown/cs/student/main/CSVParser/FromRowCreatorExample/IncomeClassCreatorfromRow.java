package edu.brown.cs.student.main.CSVParser.FromRowCreatorExample;

import edu.brown.cs.student.main.CreatorFromRow;
import edu.brown.cs.student.main.FactoryFailureException;
import java.util.List;

public class IncomeClassCreatorfromRow implements CreatorFromRow<IncomeFromRaceClass> {
  private int numberMade;

  public IncomeClassCreatorfromRow() {
    this.numberMade = 0;
  }
  /*
  This is an example of a creator for an alternate object type.  It uses numberMade to skip the first row of the file,
  as this was specifically designed for income_by_race which contains a header.
   */
  public IncomeFromRaceClass create(List<String> row) throws FactoryFailureException {
    if (this.numberMade > 0) {
      IncomeFromRaceClass toAdd = new IncomeFromRaceClass();
      try {
        toAdd.IncomeFromRaceClassCreator(
            row.get(0),
            row.get(1),
            row.get(2),
            row.get(3),
            row.get(4),
            row.get(5),
            row.get(6),
            row.get(7),
            row.get(8));
      } catch (NumberFormatException n) {
        throw new FactoryFailureException("Failure from adapting row into object", row);
      }
      this.numberMade++;
      return toAdd;
    }
    this.numberMade++;
    return new IncomeFromRaceClass();
  }
}
