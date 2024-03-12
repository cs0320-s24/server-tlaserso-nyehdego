package edu.brown.cs.student;

import edu.brown.cs.student.main.builtins.broadband.APICodeSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestACSAPI {

  @Test
  public void StateCodesTest() {
    APICodeSource tester = new APICodeSource();
    Assertions.assertTrue(tester.getCodes().get(1).get(0).equals("Alabama"));
    Assertions.assertTrue(tester.getCodes().get(1).get(1).equals("01"));
  }
}
