package edu.brown.cs.student.main.builtins;

/**
 * An object that can be used to get the percent at a county
 */

public interface ACSDatasource {

    ACSData findPercentage(ACSLocation location);

}
