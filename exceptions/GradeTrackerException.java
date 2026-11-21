package exceptions;

/**
 * Base exception for all Grade Tracker errors.
 * Enables polymorphic catch blocks for any custom exception.
 */
public class GradeTrackerException extends Exception {
    public GradeTrackerException(String message) {
        super(message);
    }
}