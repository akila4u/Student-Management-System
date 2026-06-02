package com.SMSystem;
import java.util.ArrayList;

public class StudentService {
    private ArrayList<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
        System.out.println("Student added scccessfully!");
    }

    public void displayAllStudents(){
        if(students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }
        for(Student student : students){
            student.displayStudent();
        }
    }

}
