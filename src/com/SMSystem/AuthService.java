package com.SMSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AuthService {
    private static String adminUsername;
    private static String adminPassword;

    static {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            adminUsername = prop.getProperty("admin.username");
            adminPassword = prop.getProperty("admin.password");
        } catch (IOException ex) {
            System.out.println("Config file not found!");
        }
    }

    public static User authenticate(String username, String password) {
        if (adminUsername != null && adminUsername.equalsIgnoreCase(username) && adminPassword != null && adminPassword.equals(password)) {
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
        if (adminPassword != null && adminPassword.equals(currentPass)) {
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