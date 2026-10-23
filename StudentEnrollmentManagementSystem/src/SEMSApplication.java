
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;

// Runs the Student Enrollment Management System.
public class SEMSApplication {

    private final StudentEnrollmentManagementSystem system =
            new StudentEnrollmentManagementSystem();

    private final Scanner scanner = new Scanner(System.in);

    private final DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("MM/dd/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        new SEMSApplication().run();
    }

    // Displays the main menu.
    public void run() {

        int choice;

        do {

            System.out.println("\n========== STUDENT ENROLLMENT MANAGEMENT SYSTEM MENU ==========");
            System.out.println("1. Add Student");
            System.out.println("2. Import Students");
            System.out.println("3. Find Student");
            System.out.println("4. View All Students");
            System.out.println("5. Update Student");
            System.out.println("6. Remove Student");
            System.out.println("7. Enrollment Statistics");
            System.out.println("0. Exit");

            choice = readInt("Select an option: ", 0, 7);

            switch (choice) {

                case 1:
                    addStudent();
                    break;

                case 2:
                    importStudents();
                    break;

                case 3:
                    findStudent();
                    break;

                case 4:
                    viewAllStudents();
                    break;

                case 5:
                    updateStudent();
                    break;

                case 6:
                    removeStudent();
                    break;

                case 7:
                    viewEnrollmentStatistics();
                    break;

                case 0:
                    System.out.println("Exiting SEMS. Have a great day!");
                    break;
            }

        } while (choice != 0);
    }

    // Adds one student manually.
    private void addStudent() {

        System.out.println("\n--- Add Student ---");

        int id = readNewStudentID();

        String firstName =
                readRequiredText("First Name: ");

        String lastName =
                readRequiredText("Last Name: ");

        LocalDate dob =
                readDate("Date of Birth (MM/DD/YYYY): ");

        LocalDate enrolled =
                readEnrollmentDate(dob);

        int grade =
                readGradeLevel("Grade Level (01-12): ");

        String school =
                readRequiredText("School Name: ");

        Student student = new Student(
                id, firstName, lastName, dob,
                enrolled, grade, school
        );

        if (system.addStudent(student)) {

            System.out.println(
                    "\nStudent added successfully!");

        } else {

            System.out.println(
                    "\nUnable to add student.");
        }
    }

    // Imports student records from a text file.
    private void importStudents() {

        System.out.println("\n--- Import Students ---");

        String filePath =
                readRequiredText("Enter text file path: ");

        try {

            int count = system.importStudents(filePath);

            System.out.println(
                    count + " student(s) imported successfully.");

        } catch (IOException | IllegalArgumentException exception) {

            System.out.println(
                    "Import failed. No records were added.");
            System.out.println(exception.getMessage());
        }
    }

    // Finds a student record by the student ID.
    private void findStudent() {

        System.out.println("\n--- Find Student ---");

        int id = readStudentID();

        Student student = system.findStudent(id);

        if (student == null) {

            System.out.println("Student not found.");

        } else {

            System.out.println("\n" + student);
        }
    }

    // Displays all student records.
    private void viewAllStudents() {

        System.out.println("\n--- All Students ---");

        List<Student> students = system.getAllStudents();

        if (students.isEmpty()) {

            System.out.println(
                    "No student records available.");
            return;
        }

        for (Student student : students) {

            System.out.println(student);
            System.out.println("----------------------");
        }

        System.out.println(
                "Total Students: " + students.size());
    }

    // Updates one field of an existing student record.
    private void updateStudent() {

        System.out.println("\n--- Update Student ---");

        int id = readStudentID();

        Student existing = system.findStudent(id);

        if (existing == null) {

            System.out.println("Student not found.");
            return;
        }

        System.out.println("\nCurrent Record:");
        System.out.println(existing);

        System.out.println("\nSelect Field to Update:");
        System.out.println("1. First Name");
        System.out.println("2. Last Name");
        System.out.println("3. Date of Birth");
        System.out.println("4. Date of Enrollment");
        System.out.println("5. Grade Level");
        System.out.println("6. School Name");
        System.out.println("0. Cancel");

        int choice = readInt("Select field: ", 0, 6);

        if (choice == 0) {
            System.out.println("Update canceled.");
            return;
        }

        // Copy the existing record before changing it.
        Student updated = new Student(
                existing.getStudentID(),
                existing.getFirstName(),
                existing.getLastName(),
                existing.getDateOfBirth(),
                existing.getDateOfEnrollment(),
                existing.getGradeLevel(),
                existing.getSchoolName()
        );

        switch (choice) {

            case 1:
                updated.setFirstName(
                        readRequiredText("New First Name: "));
                break;

            case 2:
                updated.setLastName(
                        readRequiredText("New Last Name: "));
                break;

            case 3:
                updated.setDateOfBirth(
                        readBirthDateBefore(
                                existing.getDateOfEnrollment()));
                break;

            case 4:
                updated.setDateOfEnrollment(
                        readEnrollmentDate(
                                existing.getDateOfBirth()));
                break;

            case 5:
                updated.setGradeLevel(
                        readGradeLevel("New Grade Level (01-12): "));
                break;

            case 6:
                updated.setSchoolName(
                        readRequiredText("New School Name: "));
                break;
        }

        if (system.updateStudent(updated)) {

            System.out.println(
                    "Student updated successfully!");

            System.out.println(
                    system.findStudent(id));

        } else {

            System.out.println(
                    "Update failed. Original record unchanged.");
        }
    }

    // Removes a student after confirmation.
    private void removeStudent() {

        System.out.println("\n--- Remove Student ---");

        int id = readStudentID();

        Student student = system.findStudent(id);

        if (student == null) {

            System.out.println("Student not found.");
            return;
        }

        System.out.println(student);

        boolean confirmed = readYesNo(
                "Confirm deletion (Y/N): ");

        if (confirmed) {

            if (system.removeStudent(id)) {

                System.out.println(
                        "Student removed successfully.");

            } else {

                System.out.println(
                        "Unable to remove student.");
            }

        } else {

            System.out.println(
                    "Deletion canceled. Student record remains unchanged.");
        }
    }

    // Displays enrollment statistics by grade.
    private void viewEnrollmentStatistics() {

        System.out.println("\n--- Enrollment Statistics ---");

        int grade =
                readGradeLevel("Enter Grade Level (01-12): ");

        int total = system.getTotalStudentCount();

        if (total == 0) {

            System.out.println(
                    "No student records available.");
            return;
        }

        int gradeCount =
                system.getStudentCountByGrade(grade);

        double percentage =
                system.calculateEnrollmentStatistics(grade);

        System.out.printf(
                "Grade %02d: %d of %d students (%.2f%%)%n",
                grade, gradeCount, total, percentage);
    }

    /*
     * VALIDATION METHODS
     * Each method has its own loop.
     * Invalid input only repeats the current field.
     */

    // Validates required text fields.
    private String readRequiredText(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(
                    "This field cannot be blank. Try again.");
        }
    }

    // Validates whole numbers within a range.
    private int readInt(String prompt, int min, int max) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {

                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException exception) {

                // Invalid input will be handled below.
            }

            System.out.println(
                    "Enter a number between "
                            + min + " and " + max + ".");
        }
    }

    // Requires exactly 8 digits for a student ID.
    private int readStudentID() {

        while (true) {

            System.out.print("Student ID (8 digits): ");

            String input = scanner.nextLine().trim();

            if (input.matches("[1-9][0-9]{7}")) {

                return Integer.parseInt(input);
            }

            System.out.println(
                    "Invalid ID. Enter exactly 8 digits.");
        }
    }

    // Requires a unique 8-digit student ID.
    private int readNewStudentID() {

        while (true) {

            int id = readStudentID();

            if (system.isStudentIDUnique(id)) {
                return id;
            }

            System.out.println(
                    "Student ID already exists. Try another.");
        }
    }

    // Requires a 2-digit grade level from 01 to 12.
    private int readGradeLevel(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (input.matches("0[1-9]|1[0-2]")) {

                return Integer.parseInt(input);
            }

            System.out.println(
                    "Invalid grade. Enter 01 through 12.");
        }
    }

    // Requires a date in exactly MM/DD/YYYY format.
    private LocalDate readDate(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (!input.matches(
                    "[0-9]{2}/[0-9]{2}/[0-9]{4}")) {

                System.out.println(
                        "Invalid format. Use MM/DD/YYYY.");
                continue;
            }

            try {

                return LocalDate.parse(input, dateFormat);

            } catch (DateTimeParseException exception) {

                System.out.println(
                        "Invalid calendar date. Try again.");
            }
        }
    }

    // Requires enrollment date to follow birth date.
    private LocalDate readEnrollmentDate(LocalDate dob) {

        while (true) {

            LocalDate enrolled = readDate(
                    "Date of Enrollment (MM/DD/YYYY): ");

            if (enrolled.isAfter(dob)) {
                return enrolled;
            }

            System.out.println(
                    "Enrollment date must be after birth date.");
        }
    }

    // Validates birth date when updating a record.
    private LocalDate readBirthDateBefore(
            LocalDate enrollmentDate) {

        while (true) {

            LocalDate dob = readDate(
                    "New Date of Birth (MM/DD/YYYY): ");

            if (dob.isBefore(enrollmentDate)) {
                return dob;
            }

            System.out.println(
                    "Birth date must be before enrollment date.");
        }
    }

    // Requires user input of Y or N for confirmation.
    private boolean readYesNo(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Y")) {
                return true;
            }

            if (input.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println(
                    "Invalid response. Enter Y or N.");
        }
    }
}
