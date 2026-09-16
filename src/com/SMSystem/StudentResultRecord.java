package com.SMSystem;

public class StudentResultRecord {
    private String subjectCode;
    private double marks;
    private String status; // "NORMAL", "REPEAT", "MC"

    public StudentResultRecord(String subjectCode, double marks, String status) {
        this.subjectCode = subjectCode;
        this.marks = marks;
        this.status = status;
    }

    public String getSubjectCode() { return subjectCode; }
    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}