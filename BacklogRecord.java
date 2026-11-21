/**
 * One backlog entry: a failed subject with attempt tracking.
 * Extends AcademicRecord for polymorphism.
 */
public class BacklogRecord extends AcademicRecord {
    private String subjectName;
    private int originalMarks;
    private int attempts;
    private int latestMarks;
    private boolean cleared;

    public BacklogRecord(int semester, String subjectName, int originalMarks) {
        super(semester);
        this.subjectName = subjectName;
        this.originalMarks = originalMarks;
        this.latestMarks = originalMarks;
        this.attempts = 1;
        this.cleared = false;
    }

    public String getSubjectName() { return subjectName; }
    public int getOriginalMarks() { return originalMarks; }
    public int getAttempts() { return attempts; }
    public int getLatestMarks() { return latestMarks; }
    public boolean isCleared() { return cleared; }

    public void recordReExam(int marks) {
        this.attempts++;
        this.latestMarks = marks;
        if (marks >= GradeCalculator.PASS_MARK) {
            this.cleared = true;
        }
    }

    @Override
    public int getTotalMarks() { return latestMarks; }

    @Override
    public void displayDetails() {
        System.out.printf("  %-10s Sem=%d Original=%3d Attempts=%d Status=%s (Latest=%d)%n",
                subjectName, semester, originalMarks, attempts,
                cleared ? "Cleared" : "Active", latestMarks);
    }
}