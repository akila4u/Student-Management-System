package com.SMSystem;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        StudentService service = new StudentService();

        //Menu Repeat
        while(true) {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student By ID");
            System.out.println("4. delete Student By ID");
            System.out.println("5. Update Student By ID");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {

                System.out.println("Enter Student ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();

                if (service.checkIdExists(id)){
                    System.out.println("Error: This ID is already taken! Please use a different ID.");
                    continue;
                }

                System.out.print("Enter Student Name: ");
                String name = scanner.nextLine(); // buffer clear

                System.out.print("Enter Student Age: ");
                int age = scanner.nextInt();
                scanner.nextLine(); //buffer clear

                System.out.print("Enter Student Course: ");
                String course = scanner.nextLine();

                Student student = new Student(id, name, age, course);
                service.addStudent(student);

            }
            else if (choice == 2) {
                service.displayAllStudents();


            } else if (choice == 3) {
                System.out.print("Enter Student ID to search: ");
                int searchId = scanner.nextInt();
                scanner.nextLine(); // buffer clear

                service.searchStudentById(searchId);

            }

            else if (choice == 4) {
                System.out.println("Enter Student ID to delete:");
                int deleteId = scanner.nextInt();
                scanner.nextLine();

                //Method call for Delete
                service.deleteStudentById(deleteId);
            }

            else if (choice == 5){
                System.out.print("Enter Student ID to update: ");
                int updateId = scanner.nextInt();
                scanner.nextLine(); // Buffer clear - nextInt()


                System.out.print("Enter new Student Name: ");
                String newName = scanner.nextLine();

                System.out.print("Enter new Student Age: ");
                int newAge = scanner.nextInt();
                scanner.nextLine(); // Buffer clear

                System.out.print("Enter new Student Course: ");
                String newCourse = scanner.nextLine();

                service.updateStudentById(updateId, newName, newAge, newCourse);

            }
            else if (choice == 6) {
                // Exit Program
                System.out.println("Exiting program...");
                break;

            }

            else {
                    System.out.println("Invalid choice! Please try again.");
                }
        }
        scanner.close();
    }
}
