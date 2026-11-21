import exceptions.*;
import java.util.*;

/**
 * Handles registration, search (manual linear search), update.
 * Uses ArrayList for storage and HashSet for duplicate-ID prevention.
 */
public class StudentManager {
    private ArrayList<Student> students;
    private HashSet<Integer> ids;

    public StudentManager() {
        this.students = new ArrayList<>();
        this.ids = new HashSet<>();
    }

    public void registerStudent(int id, String name, String dept, int year, int sem)
            throws InvalidStudentException, DuplicateStudentException {
        if (id <= 0) throw new InvalidStudentException("Student ID must be positive.");
        if (name == null || name.trim().isEmpty())
            throw new InvalidStudentException("Name cannot be empty.");
        if (sem < 1 || sem > 8)
            throw new InvalidStudentException("Semester must be between 1 and 8.");
        if (ids.contains(id))
            throw new DuplicateStudentException("Student ID already exists: " + id);

        students.add(new Student(id, name.trim(), dept, year, sem));
        ids.add(id);
    }

    // Manual Linear Search (requirement: no list.contains / streams)
    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public ArrayList<Student> getAll() { return students; }

    public void viewAll() {
        System.out.println("\n========= STUDENT RECORDS =========");
        if (students.isEmpty()) {
            System.out.println("  No students registered.");
        } else {
            System.out.printf("%-8s %-12s %-12s %-6s %-5s%n",
                    "ID", "NAME", "DEPARTMENT", "YEAR", "SEM");
            for (Student s : students) {
                System.out.printf("%-8d %-12s %-12s %-6d %-5d%n",
                        s.getId(), s.getName(), s.getDepartment(),
                        s.getYear(), s.getCurrentSemester());
            }
        }
        System.out.println("===================================\n");
    }
}