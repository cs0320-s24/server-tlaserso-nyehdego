package edu.brown.cs.student.main.builtins;

/**
 * class for a custom exception class
 */
public class DataSourceException extends Throwable {
    private final Throwable cause;
    public DataSourceException(String message) {
        super(message);
        this.cause = null;
    }
    public DataSourceException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
}
