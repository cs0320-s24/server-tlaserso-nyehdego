package edu.brown.cs.student.main.CSVParser.FromRowCreatorExample;

import static java.lang.Integer.parseInt;

public class IncomeFromRaceClass {
  public int Race_ID;
  public String race;
  public int ID_year;
  public String year;
  public int income;
  public int income_by_race_moe;
  public String Geography;
  public String Geo_ID;
  public String Slug_Geo;

  /*
  this is an example of an alternate object that a csv can be parsed into.  It stores all the information from the
  income_by_race.csv file as instance variables within an object.  These are saved in the incomefromraceclasscreator.
   */
  public IncomeFromRaceClass() {}

  public void IncomeFromRaceClassCreator(
      String r_id,
      String r,
      String ID_y,
      String y,
      String i,
      String ibrm,
      String g,
      String Geo_ID,
      String slug_geo) {

    try {
      this.Race_ID = parseInt(r_id);
      this.race = r;
      this.ID_year = parseInt(ID_y);
      this.year = y;
      this.income = parseInt(i);
      this.income_by_race_moe = parseInt(ibrm);
      this.Geography = g;
      this.Geo_ID = Geo_ID;
      this.Slug_Geo = slug_geo;
    } catch (NumberFormatException e) {
      System.out.println("Failed to cast String to Int");
      throw e;
    }
  }
}
