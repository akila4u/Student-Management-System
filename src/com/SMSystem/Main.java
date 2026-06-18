package com.SMSystem;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentService service = new StudentService();

        // Menu Repeat
        while (true) {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student By ID");
            System.out.println("4. Delete Student By ID");
            System.out.println("5. Update Student By ID");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            Integer choice = getIntInput(scanner, "Enter your choice");
            if (choice == null || choice == 0) continue;

            if (choice == 1) {
                Integer id = getIntInput(scanner, "Enter Student ID");
                if (id == null || id == 0) continue;

                if (service.checkIdExists(id)) {
                    System.out.println("Error: This ID is already taken!");
                    continue;
                }

                System.out.print("Enter Student Name: ");
                String name = scanner.nextLine();

                Integer age = getIntInput(scanner, "Enter Student Age");
                if (age == null || age == 0) continue;

                System.out.print("Enter Student Course: ");
                String course = scanner.nextLine();

                service.addStudent(new Student(id, name, age, course));

            } else if (choice == 2) {
                service.displayAllStudents();

            } else if (choice == 3) {
                Integer searchId = getIntInput(scanner, "Enter Student ID to search");
                if (searchId != null && searchId != 0) service.searchStudentById(searchId);

            } else if (choice == 4) {
                Integer deleteId = getIntInput(scanner, "Enter Student ID to delete");
                if (deleteId != null && deleteId != 0) {
                    service.deleteStudentById(deleteId);
                }

            } else if (choice == 5) {
                Integer updateId = getIntInput(scanner, "Enter Student ID to update");
                if (updateId == null || updateId == 0) continue;

                System.out.print("Enter new Student Name: ");
                String newName = scanner.nextLine();
                Integer newAge = getIntInput(scanner, "Enter new Student Age");
                if (newAge == null || newAge == 0) continue;
                System.out.print("Enter new Student Course: ");
                String newCourse = scanner.nextLine();

                service.updateStudentById(updateId, newName, newAge, newCourse);

            } else if (choice == 6) {
                System.out.println("Exiting program...");
                break;

            } else {
                System.out.println("Invalid choice! Please try again.");
            }
        }
        scanner.close();
    } // main method

    //  Helper Method
    public static Integer getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt + " (Type 0 to cancel): ");
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (Exception e) {
            System.out.println("Error: Invalid input! Please enter a number.");
            return null;
        }
    }
} // Main class last