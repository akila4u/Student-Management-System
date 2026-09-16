package com.SMSystem;

public class AcademicGrading {

    public static double calculateGP(double marks, String status) {
        if ("MC".equalsIgnoreCase(status)) {
            return -1.0; // Excluded from GPA calculation
        }

        double rawGp;
        if (marks >= 85) rawGp = 4.00;
        else if (marks >= 70) rawGp = 4.00;
        else if (marks >= 65) rawGp = 3.70;
        else if (marks >= 60) rawGp = 3.30;
        else if (marks >= 55) rawGp = 3.00;
        else if (marks >= 50) rawGp = 2.70;
        else if (marks >= 45) rawGp = 2.30;
        else if (marks >= 40) rawGp = 2.00;
        else if (marks >= 35) rawGp = 1.70;
        else if (marks >= 30) rawGp = 1.30;
        else if (marks >= 25) rawGp = 1.00;
        else rawGp = 0.00;

        // University Repeat rule: Capped at C (2.00)
        if ("REPEAT".equalsIgnoreCase(status)) {
            return Math.min(rawGp, 2.00);
        }
        return rawGp;
    }

    public static String getGradeLetter(double marks, String status) {
        if ("MC".equalsIgnoreCase(status)) return "MC";
        if ("REPEAT".equalsIgnoreCase(status)) {
            double gp = calculateGP(marks, status);
            return gp >= 2.00 ? "C (Repeat)" : "E (Repeat)";
        }
        if (marks >= 85) return "A+";
        if (marks >= 70) return "A";
        if (marks >= 65) return "A-";
        if (marks >= 60) return "B+";
        if (marks >= 55) return "B";
        if (marks >= 50) return "B-";
        if (marks >= 45) return "C+";
        if (marks >= 40) return "C";
        if (marks >= 35) return "C-";
        if (marks >= 30) return "D+";
        if (marks >= 25) return "D";
        return "E";
    }
}