package iss.kienephongthuyfvix.uniportal.controller;

import iss.kienephongthuyfvix.uniportal.dao.Database;
import iss.kienephongthuyfvix.uniportal.dao.UserDao;
import iss.kienephongthuyfvix.uniportal.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
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

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty");
            return;
        }

        try {
            Database.initialize(username, password);

            UserDao userDao = new UserDao();
            List<String> roles = userDao.getCurrentUserRoles();

            if (roles.isEmpty()) {
                showError("No roles assigned to the user");
                return;
            }

            List<String> validRoles = List.of("RL_ADMIN", "RL_TRGDV", "RL_SV", "RL_NV_CTSV", "RL_NV_PKT", "RL_NV_TCHC", "RL_NV_PDT", "RL_GV");
            roles.removeIf(role -> !validRoles.contains(role));

            if (roles.size() == 1) {
                loadDashboard(roles.getFirst());
            } else {
                String selectedRole = showRoleSelectionDialog(roles);
                if (selectedRole != null) {
                    loadDashboard(selectedRole);
                }
            }
            Session.setCurrentUsername(username);
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private String showRoleSelectionDialog(List<String> roles) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(roles.getFirst(), roles);
        dialog.setTitle("Select Role");
        dialog.setHeaderText("Multiple Roles Found");
        dialog.setContentText("Please select a role to continue:");

        return dialog.showAndWait().orElse(null);
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
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
            log.error(e.getMessage());
        }
    }
}