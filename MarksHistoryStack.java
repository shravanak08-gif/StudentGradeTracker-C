import java.util.Stack;

/**
 * USER-CREATED DATA STRUCTURE: A custom Stack (LIFO) to handle Undo operations.
 * Demonstrates manual data structure implementation.
 */
public class MarksHistoryStack {
    
    // Inner class to store the exact state of a subject before it was updated
    public static class MarksAction {
        public int studentId;
        public int semester;
        public String subject;
        public int oldMarks;

        public MarksAction(int studentId, int semester, String subject, int oldMarks) {
            this.studentId = studentId;
            this.semester = semester;
            this.subject = subject;
            this.oldMarks = oldMarks;
        }
    }

    // Using Java's built-in Stack to store our custom objects
    private Stack<MarksAction> history = new Stack<>();

    // Push an action onto the stack
    public void saveAction(int studentId, int semester, String subject, int oldMarks) {
        history.push(new MarksAction(studentId, semester, subject, oldMarks));
    }

    // Pop the last action off the stack (Undo)
    public MarksAction undoLastAction() {
        if (!history.isEmpty()) {
            return history.pop();
        }
        return null; // Nothing to undo
    }
}