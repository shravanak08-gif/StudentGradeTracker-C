import java.util.HashMap;
import java.util.Map;

/**
 * Holds all subjects for one semester of one student.
 * Uses HashMap<subjectName, Subject> for O(1) updates.
 */
public class SemesterRecord extends AcademicRecord {
    private HashMap<String, Subject> subjects;

    public SemesterRecord(int semester) {
        super(semester);
        this.subjects = new HashMap<>();
    }

    public HashMap<String, Subject> getSubjects() { return subjects; }

    public void addOrUpdateSubject(String name, int marks, int credits) {
        // If subject exists, update marks/credits; else add new
        if (subjects.containsKey(name)) {
            Subject s = subjects.get(name);
            s.setMarks(marks);
            s.setCredits(credits);
        } else {
            subjects.put(name, new Subject(name, marks, credits));
        }
    }

    public Subject getSubject(String name) {
        return subjects.get(name);
    }

    @Override
    public int getTotalMarks() {
        int total = 0;
        for (Subject s : subjects.values()) total += s.getMarks();
        return total;
    }

    public int getTotalCredits() {
        int c = 0;
        for (Subject s : subjects.values()) c += s.getCredits();
        return c;
    }

    public double getSGPA() {
        int credits = 0, weighted = 0;
        for (Subject s : subjects.values()) {
            credits += s.getCredits();
            weighted += s.getCredits() * s.getGradePoint();
        }
        return credits == 0 ? 0 : (double) weighted / credits;
    }

    @Override
    public void displayDetails() {
        System.out.println("Semester " + semester + " record:");
        for (Subject s : subjects.values()) {
            System.out.printf("  %-10s marks=%3d credits=%d grade=%s%n",
                    s.getName(), s.getMarks(), s.getCredits(), s.getGrade());
        }
    }
}