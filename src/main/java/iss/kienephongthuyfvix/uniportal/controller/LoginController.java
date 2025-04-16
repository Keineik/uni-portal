package iss.kienephongthuyfvix.uniportal.controller;

import iss.kienephongthuyfvix.uniportal.dao.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    protected void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Database.initialize("system", "sa");
        loadDashboard("Nhân viên Phòng Khảo thí");

//        if (username.isEmpty() || password.isEmpty()) {
//            showError("Username and password cannot be empty");
//            return;
//        }
//
//        try {
//            if (isValidCredentials(username, password)) {
//
//            }
            // Attempt database connection with provided credentials
//            Connection conn = Database.getConnection(username, password);
//
//            if (conn != null) {
//                // Login successful
//                loadMainApplication(username);
//            } else {
//                showError("Invalid username or password");
//            }
//        } catch (Exception e) {
//            showError("Database connection error: " + e.getMessage());
//        }
    }

    @FXML
    protected void handleReset(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        errorMessageLabel.setVisible(false);
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }

    private boolean isValidCredentials(String username, String password) {
        // Replace with actual authentication logic
        return ("admin".equals(username) && "password".equals(password)) || ("1".equals(username) && "1".equals(password));
    }

    private void loadDashboard(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.initializeRole(role);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/iss/kienephongthuyfvix/uniportal/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainApplication(String username) {
        try {
            // Here you would load your main application FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/views/Main.fxml"));
            Parent root = loader.load();

            // Get the controller and pass any necessary data
            // MainController mainController = loader.getController();
            // mainController.setCurrentUser(username);

            // Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Create new scene and set it on the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("University Management System - Logged in as: " + username);
            stage.show();
        } catch (IOException e) {
            showError("Error loading main application: " + e.getMessage());
        }
    }
}