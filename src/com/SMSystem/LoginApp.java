package com.SMSystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        show(primaryStage);
    }

    public static void show(Stage stage) {
        stage.setTitle("Talent Analytics System - Login Portal");

        Label lblTitle = new Label("System Authentication");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a365d;");

        Label lblSubtitle = new Label("Student Affairs & Workforce Analytics Portal");
        lblSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username (admin or Student ID)");
        txtUser.setPrefHeight(35);
        txtUser.setMaxWidth(280);

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.setPrefHeight(35);
        txtPass.setMaxWidth(280);

        Button btnLogin = new Button("Sign In");
        btnLogin.setPrefHeight(35);
        btnLogin.setPrefWidth(280);
        btnLogin.setStyle("-fx-background-color: #0d9488; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");

        Label lblMsg = new Label();
        lblMsg.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");

        Runnable doLogin = () -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();

            if (u.isEmpty() || p.isEmpty()) {
                lblMsg.setText("Please enter both username and password!");
                return;
            }

            User user = AuthService.authenticate(u, p);
            if (user != null) {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    StudentApp.showAdminDashboard(stage);
                } else {
                    Student st = StudentApp.findStudentById(user.getStudentId());
                    if (st != null) {
                        StudentApp.showStudentDashboard(stage, st);
                    } else {
                        lblMsg.setText("Student profile not found in master records!");
                    }
                }
            } else {
                lblMsg.setText("Invalid credentials! (Admin: admin/admin123 | Student: ID/1234)");
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        txtPass.setOnAction(e -> doLogin.run());

        VBox layout = new VBox(15, lblTitle, lblSubtitle, txtUser, txtPass, btnLogin, lblMsg);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: #f8fafc;");

        stage.setScene(new Scene(layout, 420, 360));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}