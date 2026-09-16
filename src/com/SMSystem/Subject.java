package com.SMSystem;

public class Subject {
    private String code;
    private String name;
    private int credits;
    private String degree;
    private int semester;

    public Subject(String code, String name, int credits, String degree, int semester) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.degree = degree;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
}