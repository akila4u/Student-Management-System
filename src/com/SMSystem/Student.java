package com.SMSystem;

public class Student {
    private int id;
    private String name;
    private int age;
    private String course; // Degree Programme
    private double gpa;    // Cumulative GPA

    // Optional / Legacy HRM fields
    private String targetRole;
    private String skills;
    private String certifications;

    // 1. පිරිසිදු Academic Constructor (Form එකෙන් කෙළින්ම data දාන්න)
    public Student(int id, String name, int age, String course, double gpa) {
        this(id, name, age, course, gpa, "Not Specified", "General Academic", "None");
    }

    // 2. Full Parameter Constructor (පැරණි Mock Data සහ Service class වලට)
    public Student(int id, String name, int age, String course, double gpa, String targetRole, String skills, String certifications) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.gpa = gpa;
        this.targetRole = (targetRole == null || targetRole.isEmpty()) ? "Not Specified" : targetRole;
        this.skills = (skills == null || skills.isEmpty()) ? "General" : skills;
        this.certifications = (certifications == null || certifications.isEmpty()) ? "None" : certifications;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public double getGpa() { return gpa; }
    public String getTargetRole() { return targetRole; }
    public String getSkills() { return skills; }
    public String getCertifications() { return certifications; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
}