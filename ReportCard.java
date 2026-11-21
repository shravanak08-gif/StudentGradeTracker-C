/**
 * Generates a formatted report card for a semester.
 */
public class ReportCard {
    public static void generate(Student s, int semester) {
        SemesterRecord sr = s.getSemester(semester);
        if (sr == null || sr.getSubjects().isEmpty()) {
            System.out.println("No marks recorded for semester " + semester);
            return;
        }

        System.out.println("\n============== STUDENT REPORT CARD ==============");
        System.out.println("         ACADEMIC PERFORMANCE REPORT\n");
        System.out.println("Student ID : " + s.getId());
        System.out.println("Name       : " + s.getName());
        System.out.println("Department : " + s.getDepartment());
        System.out.println("Year       : " + s.getYear());
        System.out.println("Semester   : " + semester + "\n");

        System.out.printf("%-12s %-8s %-6s %-6s %-13s%n",
                "Subject", "Credits", "Marks", "Grade", "Grade Point");
        int total = 0, count = 0;
        boolean anyFail = false;
        for (Subject sub : sr.getSubjects().values()) {
            System.out.printf("%-12s %-8d %-6d %-6s %-13d%n",
                    sub.getName(), sub.getCredits(), sub.getMarks(),
                    sub.getGrade(), sub.getGradePoint());
            total += sub.getMarks();
            count++;
            if (!sub.isPassed()) anyFail = true;
        }

        int max = count * 100;
        double percentage = max == 0 ? 0 : (double) total * 100 / max;

        System.out.println("\nTotal Marks   : " + total + "/" + max);
        System.out.printf("Percentage    : %.2f%%%n", percentage);
        System.out.println("Overall Grade : " + GradeCalculator.getOverallGrade(percentage));
        System.out.printf("SGPA          : %.2f%n", sr.getSGPA());
        System.out.printf("CGPA          : %.2f%n", s.getCGPA());
        System.out.println("Result        : " + (anyFail ? "FAIL" : "PASS"));
        System.out.println("=================================================\n");
    }
}