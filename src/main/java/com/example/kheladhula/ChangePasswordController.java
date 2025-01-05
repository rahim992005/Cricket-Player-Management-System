package com.example.kheladhula;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChangePasswordController implements ServerMessageListener {
    @FXML
    public TextField userNameField;
    @FXML
    public PasswordField newPasswordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public PasswordField oldPasswordField;
    @FXML
    public Button submitChangePassword;
    @FXML
    public Button cancelChangePassword;
    @FXML
    public Button backButton;
    @FXML
    public ImageView logoChangePassword;

    private String currentTeamname;
    private Client client; // Add a reference to the Client class


    @FXML
    private void initialize() {
        submitChangePassword.setOnAction(event -> onSubmitChangePassword());
        cancelChangePassword.setOnAction(event -> onCancelChangePassword());
        backButton.setOnAction(this::onBackButton);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setCurrentTeamname(String currentTeamname) {
        this.currentTeamname = currentTeamname;
    }

    public void onBackButton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dash-board.fxml"));
            Parent dashBoardRoot = loader.load();
            DashboardController dashboardController = loader.getController();
            dashboardController.setClient(client);
            dashboardController.setCurentTeamname(currentTeamname);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(dashBoardRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file: " + e.getMessage());
        }
    }
    public void onCancelChangePassword() {
        newPasswordField.clear();
        confirmPasswordField.clear();
        oldPasswordField.clear();
        userNameField.clear();
    }

    public void onSubmitChangePassword() {
        String username = userNameField.getText();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled!");
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match!");
            return;
        }
        client.clearListeners();
        client.addListener(this);
        client.sendMessage("changepassword:" + username + ":" + oldPassword + ":" + newPassword);
//        boolean success = changePassword(username, oldPassword, newPassword);


//        if (success) {
//            showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-screen.fxml"));
//            try {
//                Parent root = loader.load();
//                Stage stage = (Stage) submitChangePassword.getScene().getWindow();
//                stage.setScene(new Scene(root));
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Error loading FXML file: " + e.getMessage());
//            }
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password!");
//        }

    }

    @Override
    public void onServerMessage(String message) {
        System.out.println("Received message: " + message);
        if (message.startsWith("Password_change_successful")) {
           // statusLabel.setText("Login successful!");
            // Proceed to the next screen or functionality
            Platform.runLater(() -> {
                {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login-screen.fxml"));
                try {
                    Parent root = loader.load();
                    Stage stage = (Stage) submitChangePassword.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error loading FXML file: " + e.getMessage());
                }
            }
            });
        } else if (message.startsWith("Password_change_failed")) {
            //statusLabel.setText("Login failed. Please try again.");
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password!");
        }
    }

    @Override
    public void onServerMessage(List<Player> players) {
        // Not needed for this implementation
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


}
