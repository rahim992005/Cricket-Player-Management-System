package com.example.kheladhula;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoginController implements ServerMessageListener {

    @FXML
    private Label incorrectPassword;
    @FXML
    private TextField clubCodeField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private DatabaseClub databaseClub = new DatabaseClub();

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String currentTeamname;
    private Client client ;

    private ActionEvent loginEvent; // Store the ActionEvent

    public void switchToDashBoard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dash-board.fxml"));
        Parent root = loader.load();

        // Pass the currentClubUsername to the dashboard controller
        DashboardController dashboardController = loader.getController();
        dashboardController.setClient(client);
        dashboardController.setCurentTeamname(currentTeamname);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


//    public void checkLogIn(ActionEvent actionEvent) throws IOException {
//        String username = clubCodeField.getText();
//        String password = passwordField.getText();
//
//        Club club = databaseClub.getTeam(username);
//
//        if (club != null && club.getPassword().equals(password)) {
//            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome to, " + club.getTeamName() + "!");
//            currentTeamname = club.getTeamName();
//            client = new Client();
//            //client start;
//            client.startClient("127.0.0.1", 12345, currentTeamname);
//
//            // Switch to dashboard
//            switchToDashBoard(actionEvent);
//
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
//        }
//    }

    public void checkLogIn(ActionEvent actionEvent) throws IOException {
        this.loginEvent = actionEvent; // Store the ActionEvent
        String username = clubCodeField.getText();
        String password = passwordField.getText();
        Club club = databaseClub.getTeam(username);
        if (club == null) {
            showAlert(Alert.AlertType.WARNING, "Login Failed", "Club not found in the database.");
            return;
        }
        currentTeamname = club.getTeamName();

        client = new Client();
        client.startClient("localhost", 12345, username); // Adjust server address and port as needed
        client.clearListeners();
        client.addListener(this);
        client.sendMessage("LOGIN:" + username + ":" + password);
    }

    @Override
    public void onServerMessage(String message) {
        System.out.println("Received message: " + message);
        if (message.startsWith("LOGIN_SUCCESS")) {
//            statusLabel.setText("Login successful!");
            // Proceed to the next screen or functionality
            showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
            Platform.runLater(() -> {
                try {
                    switchToDashBoard(loginEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else if (message.startsWith("LOGIN_FAILURE")) {
            statusLabel.setText("Login failed. Please try again.");
            client.stopClient();
        }
    }

    @Override
    public void onServerMessage(List<Player> players) {
        // Not needed for this implementation
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

//    public String getCurrentClubUsername() {
//        return currentClubUsername;
//    }
}