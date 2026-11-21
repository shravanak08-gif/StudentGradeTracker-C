package exceptions;

/**
 * Thrown for invalid backlog operations:
 * - re-exam marks out of 0-100
 * - clearing an already-cleared backlog
 * - subject with no active backlog
 */
public class InvalidBacklogException extends GradeTrackerException {
    public InvalidBacklogException(String message) {
        super(message);
    }
}