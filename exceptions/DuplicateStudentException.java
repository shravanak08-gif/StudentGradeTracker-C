package exceptions;

public class DuplicateStudentException extends GradeTrackerException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}