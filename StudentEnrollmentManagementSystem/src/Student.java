
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Represents an individual student.
public class Student {

    // Student attributes
    private final int studentID;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfEnrollment;
    private int gradeLevel;
    private String schoolName;

    // Constructor for a student record.
    public Student(int studentID, String firstName, String lastName,
                   LocalDate dateOfBirth, LocalDate dateOfEnrollment,
                   int gradeLevel, String schoolName) {

        this.studentID = studentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.dateOfEnrollment = dateOfEnrollment;
        this.gradeLevel = gradeLevel;
        this.schoolName = schoolName;
    }

    // Getter methods
    public int getStudentID() {
        return studentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public String getSchoolName() {
        return schoolName;
    }

    // Setter methods
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setDateOfEnrollment(LocalDate dateOfEnrollment) {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    // Displays a student record.
    @Override
    public String toString() {

        DateTimeFormatter dateFormat =
                DateTimeFormatter.ofPattern("MM/dd/uuuu");

        return "Student ID: " + studentID
                + "\nName: " + getFullName()
                + "\nDate of Birth: " + dateOfBirth.format(dateFormat)
                + "\nDate of Enrollment: "
                + dateOfEnrollment.format(dateFormat)
                + "\nGrade Level: "
                + String.format("%02d", gradeLevel)
                + "\nSchool Name: " + schoolName;
    }
}
