package com.shoppingmall.controller;

import com.shoppingmall.config.ServiceFactory;
import com.shoppingmall.model.Role;
import com.shoppingmall.model.User;
import com.shoppingmall.service.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the login and sign-up screen.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField initialBalanceField;

    private final AuthenticationService authenticationService =
            ServiceFactory.authenticationService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Optional<User> userOpt = authenticationService.login(username, password);
        if (userOpt.isEmpty()) {
            showError("Login failed", "Invalid username or password.");
            return;
        }
        User user = userOpt.get();
        if (user.getRole() == Role.ADMIN) {
            navigateTo("/fxml/AdminProductView.fxml", "Admin - Products", user);
        } else {
            navigateTo("/fxml/CatalogView.fxml", "Shopping Mall - Catalog", user);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        double initialBalance = 0;
        try {
            if (!initialBalanceField.getText().isBlank()) {
                initialBalance = Double.parseDouble(initialBalanceField.getText());
            }
        } catch (NumberFormatException e) {
            showError("Invalid balance", "Please enter a valid number for initial balance.");
            return;
        }
        try {
            authenticationService.registerCustomer(username, password, fullName, email, initialBalance);
            showInfo("Registration successful", "You can now log in with your credentials.");
        } catch (RuntimeException ex) {
            showError("Registration failed", ex.getMessage());
        }
    }

    private void navigateTo(String fxml, String title, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource(fxml));
            if (loader.getLocation() == null) {
                showError("Navigation error", "FXML file not found: " + fxml);
                return;
            }
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAwareController) {
                userAwareController.setCurrentUser(user);
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(LoginController.class.getResource("/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            showError("Navigation error", "Unable to load screen: " + e.getMessage() + "\nPath: " + fxml);
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


