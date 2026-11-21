package exceptions;

public class InvalidMarksException extends GradeTrackerException {
    public InvalidMarksException(String message) {
        super(message);
    }
}