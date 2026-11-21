import exceptions.*;
import java.util.Scanner;
public class Main {
    private static StudentManager studentManager = new StudentManager();
    private static BacklogManager backlogManager = new BacklogManager(studentManager);
    private static Scanner sc = new Scanner(System.in);
    private static MarksHistoryStack historyStack = new MarksHistoryStack(); 

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
            System.out.println("9. Undo Last Marks Update"); 
            System.out.println("10. Exit");
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
                    case 9: undoLastMarksUpdate(); break; 
                    case 10: running = false; break;
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

            // --- THE CRITICAL ADDITION: SAVE OLD MARKS BEFORE UPDATING ---
            Subject existingSubject = sr.getSubject(subject);
            if (existingSubject != null) {
                // If subject already exists, save its old marks to the Stack
                historyStack.saveAction(id, sem, subject, existingSubject.getMarks());
            } else {
                // If it's a brand new subject, save 0 (or a default) as old marks
                historyStack.saveAction(id, sem, subject, -1); 
            }
            // -------------------------------------------------------------

            sr.addOrUpdateSubject(subject, marks, credits);
            backlogManager.syncFromMarks(id, sem, subject, marks);
        }

        System.out.println("Marks added successfully for Semester " + sem + "!");
    }

     private static void undoLastMarksUpdate() {
        MarksHistoryStack.MarksAction action = historyStack.undoLastAction();
        
        if (action == null) {
            System.out.println("Nothing to undo! No recent marks updates found.");
            return;
        }

        Student s = studentManager.findById(action.studentId);
        if (s == null) return; // Should not happen, but safe check

        SemesterRecord sr = s.getSemester(action.semester);
        if (sr == null) return;

        Subject sub = sr.getSubject(action.subject);
        if (sub == null) return;

        if (action.oldMarks == -1) {
            // If it was a new subject, we could remove it. 
            // For simplicity, we will just set it to 0.
            sub.setMarks(0);
            System.out.println("Undo successful! Reverted new subject '" + action.subject + "' to 0.");
        } else {
            // Revert to the previous marks
            sub.setMarks(action.oldMarks);
            System.out.println("Undo successful! Reverted '" + action.subject + "' marks back to " + action.oldMarks);
        }
        
        // Optional: If the reverted marks are below 40, we might need to re-sync backlogs.
        try {
            backlogManager.syncFromMarks(action.studentId, action.semester, action.subject, sub.getMarks());
        } catch (Exception e) {
            System.out.println("Could not sync backlog on undo.");
        }
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