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
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {

                System.out.println("Enter Student ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();

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

                System.out.println("Exiting program...");
                break;

            } else {

                System.out.println("Invalid choice! Please try again.");

            }
        }
        scanner.close();
    }
}
