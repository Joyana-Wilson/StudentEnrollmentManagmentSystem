import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Manages student records and CRUD operations.
public class StudentEnrollmentManagementSystem {

    // Stores student records in memory.
    private final ArrayList<Student> students = new ArrayList<>();

    // Used to validate dates in imported files.
    private final DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("MM/dd/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    // Validates whether the student ID is unique.
    public boolean isStudentIDUnique(int studentID) {
        return findStudent(studentID) == null;
    }

    // Validates a student record.
    public boolean isValidStudent(Student student) {

        if (student == null) {
            return false;
        }

        return student.getStudentID() >= 10000000
                && student.getStudentID() <= 99999999
                && hasText(student.getFirstName())
                && hasText(student.getLastName())
                && hasText(student.getSchoolName())
                && student.getGradeLevel() >= 1
                && student.getGradeLevel() <= 12
                && student.getDateOfBirth() != null
                && student.getDateOfEnrollment() != null
                && student.getDateOfBirth()
                .isBefore(student.getDateOfEnrollment());
    }

    // Checks that required text fields are not blank.
    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Adds a student after validation.
    public boolean addStudent(Student student) {

        if (!isValidStudent(student)
                || !isStudentIDUnique(student.getStudentID())) {
            return false;
        }

        return students.add(student);
    }

    // Finds a student by their ID.
    public Student findStudent(int studentID) {

        for (Student student : students) {

            if (student.getStudentID() == studentID) {
                return student;
            }
        }

        return null;
    }

    // Returns all student records.
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // Updates an existing student record.
    public boolean updateStudent(Student updatedStudent) {

        if (!isValidStudent(updatedStudent)) {
            return false;
        }

        Student current =
                findStudent(updatedStudent.getStudentID());

        if (current == null) {
            return false;
        }

        current.setFirstName(updatedStudent.getFirstName());
        current.setLastName(updatedStudent.getLastName());
        current.setDateOfBirth(updatedStudent.getDateOfBirth());
        current.setDateOfEnrollment(
                updatedStudent.getDateOfEnrollment());
        current.setGradeLevel(updatedStudent.getGradeLevel());
        current.setSchoolName(updatedStudent.getSchoolName());

        return true;
    }

    // Removes a student using their ID.
    public boolean removeStudent(int studentID) {

        Student student = findStudent(studentID);

        return student != null && students.remove(student);
    }

    // Returns the total number of student records.
    public int getTotalStudentCount() {
        return students.size();
    }

    // Counts students in a selected grade.
    public int getStudentCountByGrade(int gradeLevel) {

        int count = 0;

        for (Student student : students) {

            if (student.getGradeLevel() == gradeLevel) {
                count++;
            }
        }

        return count;
    }

    // Calculates enrollment percentage for a grade.
    public double calculateEnrollmentStatistics(int gradeLevel) {

        if (students.isEmpty()) {
            return 0.0;
        }

        return (getStudentCountByGrade(gradeLevel) * 100.0)
                / students.size();
    }

    /*
     * Imports student records from a text file.
     *
     * File format:
     * ID,FirstName,LastName,DOB,EnrollmentDate,Grade,School
     *
     * Example:
     * 90001001,John,Smith,05/12/2010,08/10/2024,09,Sommerville High
     *
     * If any row is invalid, no students are imported.
     */
    public int importStudents(String filePath) throws IOException {

        List<String> lines = Files.readAllLines(Path.of(filePath));

        ArrayList<Student> pendingStudents = new ArrayList<>();

        Set<Integer> importedIDs = new HashSet<>();

        int lineNumber = 0;

        for (String line : lines) {

            lineNumber++;

            // Skip blank lines.
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] fields = line.split(",", -1);

            if (fields.length != 7) {
                throw new IllegalArgumentException(
                        "Line " + lineNumber
                                + ": Expected exactly 7 fields.");
            }

            // Keep the original text for format validation.
            String idText = fields[0].trim();
            String dobText = fields[3].trim();
            String enrollmentText = fields[4].trim();
            String gradeText = fields[5].trim();

            // Student ID must contain exactly 8 digits.
            if (!idText.matches("[1-9][0-9]{7}")) {

                throw new IllegalArgumentException(
                        "Line " + lineNumber
                                + ": Student ID must be exactly 8 digits.");
            }

            // Dates must follow MM/DD/YYYY.
            if (!dobText.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")
                    || !enrollmentText.matches(
                    "[0-9]{2}/[0-9]{2}/[0-9]{4}")) {

                throw new IllegalArgumentException(
                        "Line " + lineNumber
                                + ": Dates must use MM/DD/YYYY.");
            }

            // Grade must be exactly two digits: 01 through 12.
            if (!gradeText.matches("0[1-9]|1[0-2]")) {

                throw new IllegalArgumentException(
                        "Line " + lineNumber
                                + ": Grade must be between 01 and 12.");
            }

            try {

                int id = Integer.parseInt(idText);

                LocalDate dob =
                        LocalDate.parse(dobText, dateFormat);

                LocalDate enrolled =
                        LocalDate.parse(enrollmentText, dateFormat);

                int grade = Integer.parseInt(gradeText);

                Student student = new Student(
                        id,
                        fields[1].trim(),
                        fields[2].trim(),
                        dob,
                        enrolled,
                        grade,
                        fields[6].trim()
                );

                // Validate all student information.
                if (!isValidStudent(student)) {

                    throw new IllegalArgumentException(
                            "Invalid student information.");
                }

                // Prevent duplicate IDs within the file
                // and against existing records.
                if (!importedIDs.add(id)
                        || !isStudentIDUnique(id)) {

                    throw new IllegalArgumentException(
                            "Duplicate Student ID: " + id);
                }

                pendingStudents.add(student);

            } catch (DateTimeParseException exception) {

                throw new IllegalArgumentException(
                        "Line " + lineNumber
                                + ": Invalid calendar date.");

            } catch (IllegalArgumentException exception) {

                throw new IllegalArgumentException(
                        "Line " + lineNumber
                                + ": " + exception.getMessage());
            }
        }

        if (pendingStudents.isEmpty()) {

            throw new IllegalArgumentException(
                    "The file contains no student records.");
        }

        // Import only after all records pass validation.
        students.addAll(pendingStudents);

        return pendingStudents.size();
    }
}

