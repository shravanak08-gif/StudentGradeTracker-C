import java.util.HashMap;

/**
 * Core student entity. Stores semester records in HashMap keyed by semester number.
 */
public class Student {
    private int id;
    private String name;
    private String department;
    private int year;
    private int currentSemester;
    private HashMap<Integer, SemesterRecord> semesterRecords;

    public Student(int id, String name, String department, int year, int currentSemester) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.year = year;
        this.currentSemester = currentSemester;
        this.semesterRecords = new HashMap<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public int getCurrentSemester() { return currentSemester; }
    public HashMap<Integer, SemesterRecord> getSemesterRecords() { return semesterRecords; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(int year) { this.year = year; }
    public void setCurrentSemester(int semester) { this.currentSemester = semester; }

    public SemesterRecord getOrCreateSemester(int sem) {
        return semesterRecords.computeIfAbsent(sem, SemesterRecord::new);
    }

    public SemesterRecord getSemester(int sem) {
        return semesterRecords.get(sem);
    }

    public double getCGPA() {
        int totalCredits = 0;
        double totalWeighted = 0;
        for (SemesterRecord sr : semesterRecords.values()) {
            for (Subject s : sr.getSubjects().values()) {
                totalCredits += s.getCredits();
                totalWeighted += s.getCredits() * s.getGradePoint();
            }
        }
        return totalCredits == 0 ? 0 : totalWeighted / totalCredits;
    }

    @Override
    public String toString() {
        return String.format("ID=%d Name=%s Dept=%s Year=%d Sem=%d",
                id, name, department, year, currentSemester);
    }
}