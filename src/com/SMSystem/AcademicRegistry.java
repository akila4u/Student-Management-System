package com.SMSystem;

import java.util.*;

public class AcademicRegistry {

    public static final Map<String, List<String>> FACULTY_DEGREE_MAP = new LinkedHashMap<>();
    private static final List<String> customDegrees = new ArrayList<>();

    static {
        FACULTY_DEGREE_MAP.put("Faculty of Animal Science and Export Agriculture", Arrays.asList(
                "Animal Science",
                "Export Agriculture",
                "Tea Technology and Value Addition",
                "Palm and Latex Technology and Value Addition",
                "Aquatic Resources and Technology"
        ));

        FACULTY_DEGREE_MAP.put("Faculty of Applied Sciences / Science and Technology", Arrays.asList(
                "Computer Science and Technology",
                "Science and Technology",
                "Mineral Resources and Technology",
                "Industrial Information Technology"
        ));

        FACULTY_DEGREE_MAP.put("Faculty of Management", Arrays.asList(
                "Entrepreneurship and Management",
                "Hospitality, Tourism and Events Management"
        ));

        FACULTY_DEGREE_MAP.put("Faculty of Technological Studies & Faculty of Medicine", Arrays.asList(
                "Information and Communication Technology (ICT)",
                "Engineering Technology",
                "Biosystems Technology",
                "Medicine"
        ));
    }

    public static List<String> getAllDegrees() {
        List<String> degrees = new ArrayList<>();
        for (List<String> list : FACULTY_DEGREE_MAP.values()) {
            degrees.addAll(list);
        }
        degrees.addAll(customDegrees);
        return degrees;
    }

    public static boolean addNewDegree(String newDegree) {
        if (newDegree == null || newDegree.trim().isEmpty()) return false;
        String trimmed = newDegree.trim();
        for (String deg : getAllDegrees()) {
            if (deg.equalsIgnoreCase(trimmed)) {
                return false;
            }
        }
        customDegrees.add(trimmed);
        return true;
    }
}