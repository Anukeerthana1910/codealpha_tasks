import java.util.Scanner;
public class StudentGradeTracker {
    private int[][] marks;
    private int numSubjects;
    public StudentGradeTracker(int numStudents, int numSubjects) 
    {
        marks = new int[numStudents][numSubjects];
        this.numSubjects = numSubjects;
    }
    public void addMarks(int studentIndex, int subjectIndex, int mark) 
    {
        marks[studentIndex][subjectIndex] = mark;
    }
    public double calculateAverageForStudent(int studentIndex) 
    {
        int sum = 0;
        for (int i = 0; i < numSubjects; i++) 
        {
            sum += marks[studentIndex][i];
        }
        return (double) sum / numSubjects;
    }
    public double calculateOverallAverage() 
    {
        int totalSum = 0;
        int numStudents = marks.length;
        for (int i = 0; i < numStudents; i++) 
        {
            for (int j = 0; j < numSubjects; j++) 
            {
                totalSum += marks[i][j];
            }
        }
        return (double) totalSum / (numStudents * numSubjects);
    }
    public String getLetterGrade(double average) {
        if (average >= 95) {
            return "A+";
        } else if (average >= 90) {
            return "A";
        } else if (average >= 85) {
            return "B+";
        } else if (average >= 80) {
            return "B";
        } else if (average >= 75) {
            return "C+";
        } else if (average >= 70) {
            return "C";
        } else if (average >= 65) {
            return "D+";
        } else if (average >= 60) {
            return "D";
        } else {
            return "F";
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of students:");
        int numStudents = scanner.nextInt();
        System.out.println("Enter the number of subjects:");
        int numSubjects = scanner.nextInt();
        StudentGradeTracker tracker = new StudentGradeTracker(numStudents, numSubjects);
        for (int i = 0; i < numStudents; i++) 
        {
            System.out.println("Enter marks for student " + (i + 1) + ": ");
            for (int j = 0; j < numSubjects; j++) 
            {
                System.out.println("Subject " + (j + 1) + ": ");
                int mark = scanner.nextInt();
                tracker.addMarks(i, j, mark);
            }
        }
        System.out.println("\nStudent Grades:");
        for (int i = 0; i < numStudents; i++) 
        {
            double average = tracker.calculateAverageForStudent(i);
            String letterGrade = tracker.getLetterGrade(average);
            System.out.println("Student " + (i + 1) + ": Average = " + average + ", Grade = " + letterGrade);
        }
        double overallAverage = tracker.calculateOverallAverage();
        String overallGrade = tracker.getLetterGrade(overallAverage);
        System.out.println("\nOverall Class Average: " + overallAverage);
        System.out.println("Overall Class Grade: " + overallGrade);
        scanner.close();
    }
}
