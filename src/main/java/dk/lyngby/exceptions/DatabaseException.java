package dk.lyngby.exceptions;

public class DatabaseException extends Exception{

    public DatabaseException(Exception ex, String message) {
        super(message, ex);
    }
}
