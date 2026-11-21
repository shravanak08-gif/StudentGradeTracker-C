/**
 * Abstract base demonstrating abstraction + inheritance.
 * SemesterRecord and BacklogRecord both extend this.
 */
public abstract class AcademicRecord {
    protected int semester;

    public AcademicRecord(int semester) {
        this.semester = semester;
    }

    public int getSemester() { return semester; }

    // Abstract methods -> polymorphic behavior in subclasses
    public abstract void displayDetails();
    public abstract int getTotalMarks();
}