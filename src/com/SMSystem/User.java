package com.SMSystem;

public class User {
    private String username;
    private String password;
    private String role; // "ADMIN" or "STUDENT"
    private String studentId; // Student කෙනෙක් නම් අදාළ ID එක, Admin නම් null

    public User(String username, String password, String role, String studentId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getStudentId() { return studentId; }
}