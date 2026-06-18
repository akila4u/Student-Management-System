package com.SMSystem;

import java.io.*;
import java.util.ArrayList;

public class StudentService {
    private ArrayList<Student> students;

    // 1. Constructor:
    public StudentService() {
        this.students = new ArrayList<>();
        loadFromFile();
    }

    // 2. add new student
    public void addStudent(Student student) {
        students.add(student);
        saveToFile();
    }

    // 3. ID to delete student
    public void deleteStudentById(int id) {
        students.removeIf(s -> s.getId() == id);
        saveToFile();
    }

    // 4. ID to update student
    public void updateStudentById(int id, String newName, int newAge, String newCourse) {
        for (Student s : students) {
            if (s.getId() == id) {
                s.setName(newName);
                s.setAge(newAge);
                s.setCourse(newCourse);
                saveToFile();
                break;
            }
        }
    }

    // 5. search
    public void searchStudentById(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                System.out.println("Student Found -> ID: " + s.getId() + ", Name: " + s.getName() + ", Age: " + s.getAge() + ", Course: " + s.getCourse());
                return;
            }
        }
        System.out.println("Error: Student with ID " + id + " not found!");
    }

    // 6. show all students
    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student s : students) {
                System.out.println("ID: " + s.getId() + " | Name: " + s.getName() + " | Age: " + s.getAge() + " | Course: " + s.getCourse());
            }
        }
    }

    // 7. check ID
    public boolean checkIdExists(int id) {
        for (Student s : students) {
            if (s.getId() == id) return true;
        }
        return false;
    }

    // 8. data Save  (Persistence)
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("students.txt"))) {
            for (Student s : students) {
                writer.println(s.getId() + "," + s.getName() + "," + s.getAge() + "," + s.getCourse());
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // 9. file load
    public void loadFromFile() {
        students.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    students.add(new Student(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]), parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Starting fresh: No data file found.");
        }
    }
}