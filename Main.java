import exceptions.*;
import java.util.Scanner;

/**
 * Menu-driven console entry point.
 * Wires StudentManager and BacklogManager together and demonstrates the fix:
 *  - Save marks -> backlog auto-created only once (no duplicates on re-save)
 *  - Re-exam clears backlog AND updates semester marks for consistent CGPA
 */
public class Main {
    private static StudentManager studentManager = new StudentManager();
    private static BacklogManager backlogManager = new BacklogManager(studentManager);
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("===== STUDENT INFORMATION & GRADE TRACKER =====");
            System.out.println("1. Register Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Enter / Update Marks");
            System.out.println("5. Generate Report Card");
            System.out.println("6. View Backlogs (Backlog Management)");
            System.out.println("7. Record Re-exam Marks");
            System.out.println("8. List Students With Active Backlogs");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            int choice = readInt();
            try {
                switch (choice) {
                    case 1: register(); break;
                    case 2: studentManager.viewAll(); break;
                    case 3: search(); break;
                    case 4: enterMarks(); break;
                    case 5: reportCard(); break;
                    case 6: viewBacklogs(); break;
                    case 7: recordReExam(); break;
                    case 8: backlogManager.listStudentsWithActiveBacklogs(); break;
                    case 9: running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (GradeTrackerException ex) {
                // Single catch block handles any custom exception (polymorphism)
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void register() throws GradeTrackerException {
        System.out.print("Enter Student ID: ");
        int id = readInt();
        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine();
        System.out.print("Enter Year: ");
        int year = readInt();
        System.out.print("Enter Semester: ");
        int sem = readInt();
        studentManager.registerStudent(id, name, dept, year, sem);
        System.out.println("Student registered successfully!");
    }

    private static void search() {
        System.out.print("Enter Student ID: ");
        int id = readInt();
        Student s = studentManager.findById(id);
        if (s == null) {
            System.out.println("Student not found!");
        } else {
            System.out.println("\nStudent Found!");
            System.out.println("ID: " + s.getId());
            System.out.println("Name: " + s.getName());
            System.out.println("Department: " + s.getDepartment());
            System.out.println("Year: " + s.getYear());
            System.out.println("Semester: " + s.getCurrentSemester());
        }
    }

    private static void enterMarks() throws GradeTrackerException {
        System.out.print("Student ID: ");
        int id = readInt();
        Student s = studentManager.findById(id);
        if (s == null) throw new InvalidStudentException("Student not found: " + id);

        System.out.print("Enter Semester: ");
        int sem = readInt();
        System.out.print("Enter Number of Subjects: ");
        int n = readInt();
        if (n <= 0) throw new InvalidMarksException("Number of subjects must be positive.");

        SemesterRecord sr = s.getOrCreateSemester(sem);

        for (int i = 1; i <= n; i++) {
            System.out.print("Subject: ");
            String subject = sc.nextLine();
            System.out.print("Credits: ");
            int credits = readInt();
            System.out.print("Marks: ");
            int marks = readInt();

            if (marks < 0 || marks > 100)
                throw new InvalidMarksException("Marks must be between 0 and 100.");
            if (credits <= 0)
                throw new InvalidMarksException("Credits must be positive.");

            sr.addOrUpdateSubject(subject, marks, credits);

            // Sync backlog: creates at most ONE record per (student, subject, sem)
            backlogManager.syncFromMarks(id, sem, subject, marks);
        }

        System.out.println("Marks added successfully for Semester " + sem + "!");
    }

    private static void reportCard() {
        System.out.print("Student ID: ");
        int id = readInt();
        Student s = studentManager.findById(id);
        if (s == null) { System.out.println("Student not found!"); return; }
        System.out.print("Enter Semester: ");
        int sem = readInt();
        ReportCard.generate(s, sem);
    }

    private static void viewBacklogs() throws GradeTrackerException {
        System.out.print("Student ID: ");
        int id = readInt();
        backlogManager.displayBacklogs(id);
    }

    private static void recordReExam() throws GradeTrackerException {
        System.out.print("Student ID: ");
        int id = readInt();
        System.out.print("Enter Semester: ");
        int sem = readInt();
        System.out.print("Enter Subject: ");
        String subject = sc.nextLine();
        System.out.print("Enter Re-exam Marks: ");
        int marks = readInt();

        backlogManager.recordReExam(id, subject, sem, marks);
        System.out.println("Re-exam recorded successfully!");
        backlogManager.displayBacklogs(id);
    }

    /** Safe int reader that consumes leftover newline. */
    private static int readInt() {
        while (true) {
            try {
                String line = sc.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}