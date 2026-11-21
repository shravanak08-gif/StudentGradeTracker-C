import exceptions.*;
import java.util.*;

/**
 * Manages backlogs with duplicate prevention and consistency updates.
 *
 * KEY FIX: before creating a backlog on marks-save, we check whether one
 * already exists for (studentId, subject, semester). This avoids duplicate
 * entries when the admin edits and saves marks multiple times.
 *
 * CLEARING FIX: when a re-exam clears a backlog, we also update the original
 * SemesterRecord subject marks so the report card, SGPA and CGPA reflect the
 * latest result.
 */
public class BacklogManager {
    // student ID -> list of backlog records
    private HashMap<Integer, ArrayList<BacklogRecord>> backlogMap;
    private StudentManager studentManager;

    public BacklogManager(StudentManager studentManager) {
        this.backlogMap = new HashMap<>();
        this.studentManager = studentManager;
    }

    /**
     * Called whenever marks are saved for a subject.
     * Creates a backlog ONLY if the subject failed AND no matching backlog
     * (same student + subject + semester) already exists.
     */
    public void syncFromMarks(int studentId, int semester, String subjectName, int marks)
            throws InvalidBacklogException {
        if (marks >= GradeCalculator.PASS_MARK) {
            // If student somehow had an active backlog but marks are now passing
            // (e.g. admin edited original marks), mark it cleared automatically.
            BacklogRecord existing = findBacklog(studentId, subjectName, semester);
            if (existing != null && !existing.isCleared()) {
                existing.recordReExam(marks);
            }
            return;
        }

        // Marks below pass mark: ensure exactly ONE active backlog exists.
        BacklogRecord existing = findBacklog(studentId, subjectName, semester);
        if (existing != null) {
            // Already tracked; nothing to do. This is the duplicate-prevention guard.
            return;
        }

        BacklogRecord record = new BacklogRecord(semester, subjectName, marks);
        backlogMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(record);
    }

    /**
     * Manual linear search across a student's backlog list
     * to locate a specific subject in a specific semester.
     */
    public BacklogRecord findBacklog(int studentId, String subject, int semester) {
        ArrayList<BacklogRecord> list = backlogMap.get(studentId);
        if (list == null) return null;
        for (BacklogRecord br : list) {                     // Linear Search
            if (br.getSubjectName().equalsIgnoreCase(subject)
                    && br.getSemester() == semester) {
                return br;
            }
        }
        return null;
    }

    /**
     * Records a re-exam attempt. If marks >= PASS_MARK, clears the backlog
     * AND updates the original SemesterRecord so downstream calculations
     * (report card, SGPA, CGPA) stay consistent.
     */
    public void recordReExam(int studentId, String subject, int semester, int reExamMarks)
            throws InvalidBacklogException, InvalidStudentException {
        if (reExamMarks < 0 || reExamMarks > 100) {
            throw new InvalidBacklogException("Re-exam marks must be between 0 and 100.");
        }

        BacklogRecord br = findBacklog(studentId, subject, semester);
        if (br == null) {
            throw new InvalidBacklogException(
                    "No backlog found for subject '" + subject + "' in semester " + semester + ".");
        }
        if (br.isCleared()) {
            throw new InvalidBacklogException(
                    "Backlog for '" + subject + "' is already cleared.");
        }

        br.recordReExam(reExamMarks);

        // Consistency fix: propagate latest marks into the semester record.
        if (br.isCleared()) {
            Student s = studentManager.findById(studentId);
            if (s != null) {
                SemesterRecord sr = s.getSemester(semester);
                if (sr != null) {
                    Subject sub = sr.getSubject(subject);
                    if (sub != null) {
                        sub.setMarks(reExamMarks);
                    } else {
                        // Subject missing in record (edge case) -> recreate it
                        sr.addOrUpdateSubject(subject, reExamMarks, 3);
                    }
                }
            }
        }
    }

    public void displayBacklogs(int studentId) throws InvalidStudentException {
        Student s = studentManager.findById(studentId);
        if (s == null) throw new InvalidStudentException("Student not found: " + studentId);

        System.out.println("\n========== BACKLOG MANAGEMENT ==========");
        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + s.getName());

        ArrayList<BacklogRecord> list = backlogMap.get(studentId);
        if (list == null || list.isEmpty()) {
            System.out.println("\nStatus: No Backlogs");
            System.out.println("========================================\n");
            return;
        }

        System.out.printf("%-10s %-5s %-15s %-10s %-25s%n",
                "Subject", "Sem", "Original Marks", "Attempts", "Status");
        int active = 0, cleared = 0;
        for (BacklogRecord br : list) {                      // Linear iteration
            String status = br.isCleared()
                    ? "Cleared (Re-exam: " + br.getLatestMarks() + ")"
                    : "Active";
            System.out.printf("%-10s %-5d %-15d %-10d %-25s%n",
                    br.getSubjectName(), br.getSemester(),
                    br.getOriginalMarks(), br.getAttempts(), status);
            if (br.isCleared()) cleared++; else active++;
        }
        System.out.println("\nActive Backlogs : " + active);
        System.out.println("Cleared Backlogs: " + cleared);
        System.out.println("Status: " + (active > 0 ? "Backlog Pending" : "No Backlogs"));
        System.out.println("========================================\n");
    }

    /** Lists all students who currently have at least one active backlog. */
    public void listStudentsWithActiveBacklogs() {
        System.out.println("\n===== STUDENTS WITH ACTIVE BACKLOGS =====");
        boolean any = false;
        for (Map.Entry<Integer, ArrayList<BacklogRecord>> e : backlogMap.entrySet()) {
            boolean hasActive = false;
            for (BacklogRecord br : e.getValue()) {
                if (!br.isCleared()) { hasActive = true; break; }
            }
            if (hasActive) {
                Student s = studentManager.findById(e.getKey());
                System.out.println("  " + e.getKey() + " - " + (s != null ? s.getName() : "?"));
                any = true;
            }
        }
        if (!any) System.out.println("  (none)");
        System.out.println("=========================================\n");
    }
}