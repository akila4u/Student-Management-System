package com.SMSystem;

import java.io.*;
import java.util.ArrayList;

public class StudentService {
    private ArrayList<Student> students;

    public StudentService() {
        this.students = new ArrayList<>();
        loadFromFile();
    }

    public ArrayList<Student> getAllStudents() {
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
        saveToFile();
    }

    public void deleteStudentById(int id) {
        students.removeIf(s -> s.getId() == id);
        saveToFile();
    }

    public void updateStudentById(int id, String newName, int newAge, String newCourse, double newGpa, String newTargetRole, String newSkills, String newCertifications) {
        for (Student s : students) {
            if (s.getId() == id) {
                s.setName(newName);
                s.setAge(newAge);
                s.setCourse(newCourse);
                s.setGpa(newGpa);
                s.setTargetRole(newTargetRole);
                s.setSkills(newSkills);
                s.setCertifications(newCertifications);
                saveToFile();
                break;
            }
        }
    }

    public boolean checkIdExists(int id) {
        for (Student s : students) {
            if (s.getId() == id) return true;
        }
        return false;
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("students.txt"))) {
            for (Student s : students) {
                writer.println(
                        s.getId() + "," +
                                s.getName() + "," +
                                s.getAge() + "," +
                                s.getCourse() + "," +
                                s.getGpa() + "," +
                                s.getTargetRole() + "," +
                                s.getSkills() + "," +
                                s.getCertifications()
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        students.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int age = Integer.parseInt(parts[2]);
                    String course = parts[3];

                    double gpa = 0.0;
                    String targetRole = "Not Specified";
                    String skills = "";
                    String certifications = "";

                    if (parts.length >= 8) {
                        gpa = Double.parseDouble(parts[4]);
                        targetRole = parts[5];
                        skills = parts[6];
                        certifications = parts[7];
                    }

                    students.add(new Student(id, name, age, course, gpa, targetRole, skills, certifications));
                }
            }
        } catch (IOException e) {
            System.out.println("Starting fresh: No data file found.");
        }
    }
}