package com.SMSystem;
import java.util.ArrayList;

public class StudentService {



    private ArrayList<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
        System.out.println("Student added scccessfully!");
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }
        for (Student student : students) {
            student.displayStudent();
        }
    }

    public void searchStudentById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                System.out.println("Student Found!");
                student.displayStudent();
                return;
            }
        }
        System.out.println("Student not found!");

    }

    public void deleteStudentById(int id){
        for(Student student : students) {
            if (student.getId() == id) {
                students.remove(student);
                System.out.println("Student deleted successfully!");
                return;
            }
        }
        System.out.println("Student not found!");
    }

    public void updateStudentById(int id, String newName, int newAge, String newCourse) {
        // Loop through each student in the list
        for (Student student : students) {

            // If the student with the matching ID is found
            if (student.getId() == id) {

                // Update the details using the Setter methods
                student.setName(newName);
                student.setAge(newAge);
                student.setCourse(newCourse);

                System.out.println("Student details updated successfully!");
                return; // Exit the method since the update is done
            }
        }

        System.out.println("Student not found! Update failed.");
    }

    public boolean checkIdExists(int id) {
        //check the list
        for (Student student : students){
            //if equl in id
            if(Student.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
