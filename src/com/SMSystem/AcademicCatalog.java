package com.SMSystem;

import java.util.*;

public class AcademicCatalog {

    public static final List<Subject> SUBJECTS = new ArrayList<>();

    static {
        // BICT (Faculty of Technological Studies) - Semester 1
        SUBJECTS.add(new Subject("ESD 121-1", "English Language", 1, "Information and Communication Technology (ICT)", 1));
        SUBJECTS.add(new Subject("ESD 151-1", "Sinhala Language I / Tamil Language I", 1, "Information and Communication Technology (ICT)", 1));
        SUBJECTS.add(new Subject("ICT 101-2", "Mathematics for ICT", 2, "Information and Communication Technology (ICT)", 1));
        SUBJECTS.add(new Subject("ICT 131-3", "Programming Techniques", 3, "Information and Communication Technology (ICT)", 1));
        SUBJECTS.add(new Subject("ICT 132-2", "Fundamentals of Computer Networks", 2, "Information and Communication Technology (ICT)", 1));
        SUBJECTS.add(new Subject("ICT 133-3", "Computer Systems Organization", 3, "Information and Communication Technology (ICT)", 1));
        SUBJECTS.add(new Subject("ICT 141-3", "Electronics for ICT", 3, "Information and Communication Technology (ICT)", 1));

        // BICT - Semester 2
        SUBJECTS.add(new Subject("ICT 121-2", "Object Oriented Programming", 2, "Information and Communication Technology (ICT)", 2));
        SUBJECTS.add(new Subject("ICT 122-3", "Data Structures & Algorithms", 3, "Information and Communication Technology (ICT)", 2));
        SUBJECTS.add(new Subject("ICT 123-2", "Database Management Systems", 2, "Information and Communication Technology (ICT)", 2));

        // Computer Science and Technology (Applied Sciences) - Semester 1
        SUBJECTS.add(new Subject("CST 101-3", "Fundamentals of Computer Science", 3, "Computer Science and Technology", 1));
        SUBJECTS.add(new Subject("CST 111-3", "Structured Programming", 3, "Computer Science and Technology", 1));
        SUBJECTS.add(new Subject("CST 121-2", "Discrete Mathematics", 2, "Computer Science and Technology", 1));
    }

    public static List<Subject> getSubjects(String degree, int semester) {
        List<Subject> result = new ArrayList<>();
        if (degree == null) return result;
        for (Subject s : SUBJECTS) {
            if (s.getDegree().equalsIgnoreCase(degree) && s.getSemester() == semester) {
                result.add(s);
            }
        }
        return result;
    }

    public static void registerSubject(Subject s) {
        SUBJECTS.add(s);
    }

    public static void deleteSubject(Subject s) {
        SUBJECTS.remove(s);
    }
}