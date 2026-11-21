package exceptions;

public class InvalidStudentException extends GradeTrackerException {
    public InvalidStudentException(String message) {
        super(message);
    }
}