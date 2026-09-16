package com.SMSystem;

public class AuthService {
    private static String adminUsername = "admin";
    private static String adminPassword = "admin123";

    public static User authenticate(String username, String password) {
        if (adminUsername.equalsIgnoreCase(username) && adminPassword.equals(password)) {
            return new User(adminUsername, adminPassword, "ADMIN", null);
        }

        if (!username.isEmpty() && "1234".equals(password)) {
            Student st = StudentApp.findStudentById(username);
            if (st != null) {
                return new User(username, password, "STUDENT", String.valueOf(st.getId()));
            }
        }
        return null;
    }

    public static boolean updateAdminCredentials(String currentPass, String newUsername, String newPass) {
        if (adminPassword.equals(currentPass)) {
            if (newUsername != null && !newUsername.trim().isEmpty()) {
                adminUsername = newUsername.trim();
            }
            if (newPass != null && !newPass.trim().isEmpty()) {
                adminPassword = newPass.trim();
            }
            return true;
        }
        return false;
    }
}