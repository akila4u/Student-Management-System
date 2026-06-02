package com.SMSystem;

public class Student {

    private int id;
    private String name;
    private int age;
    private String course;

    public Student(int id, String name, int age, String course) {

        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public void displayStudent() {

        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Course: " + course);

        System.out.println(".....................");
    }


}
