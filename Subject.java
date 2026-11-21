public class Subject {
    private String name;
    private int marks;
    private int credits;

    public Subject(String name, int marks, int credits) {
        this.name = name;
        this.marks = marks;
        this.credits = credits;
    }

    public String getName() { return name; }
    public int getMarks() { return marks; }
    public int getCredits() { return credits; }

    public void setMarks(int marks) { this.marks = marks; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getGrade() { return GradeCalculator.getGrade(marks); }
    public int getGradePoint() { return GradeCalculator.getGradePoint(marks); }
    public boolean isPassed() { return marks >= GradeCalculator.PASS_MARK; }
}