package edu.brown.cs.student.main.builtins;

public record ACSLocation(String state, String county) {
    public String toOurServerParams() {
        return "state"+state +"county="+county;
    }
}
