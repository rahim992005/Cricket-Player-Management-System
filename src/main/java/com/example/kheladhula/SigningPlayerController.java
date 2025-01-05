package com.example.kheladhula;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SigningPlayerController implements ServerMessageListener {
    @FXML
    TextField textFieldPlayerName;
    @FXML
    MenuButton menuButtonCountry;
    @FXML
    TextField textFieldClubName;
    @FXML
    MenuButton menuButtonPosition;
    @FXML
    TextField textFieldWeeklySalary;
    @FXML
    TextField textFieldAge;
    @FXML
    TextField textFieldHeight;
    @FXML
    TextField textFieldJersyNumber;

    @FXML
    AnchorPane anchorBackground;
    @FXML
    AnchorPane anchorBackground1;
    @FXML
    ImageView imageLogo;
    @FXML
    Label labelClubName;

    @FXML
    Label labelMyClub;

    @FXML
    MenuItem miAfghanistan;
    @FXML
    MenuItem miAustralia;
    @FXML
    MenuItem miBangladesh;
    @FXML
    MenuItem miEngland;
    @FXML
    MenuItem miIndia;
    @FXML
    MenuItem miNewzealand;
    @FXML
    MenuItem miPakistan;
    @FXML
    MenuItem miSouthafrica;
    @FXML
    MenuItem miSrilanka;
    @FXML
    MenuItem miWestindies;

    @FXML
    MenuItem miBatsman;
    @FXML
    MenuItem miBowler;
    @FXML
    MenuItem miAllrounder;
    @FXML
    MenuItem miWicketkeeper;

    @FXML
    Label labelWarning;
    @FXML
    Label netWorth;
    @FXML
    Label balance;

    private ScheduledExecutorService scheduler;

    private Client client;
    private String currentTeamname;
    private Club currentClub;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private void setButtonBack(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dash-board.fxml"));
        Parent root = loader.load();

        DashboardController dashboardController = loader.getController();
        dashboardController.setClient(client);
        dashboardController.setCurentTeamname(currentTeamname);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    private void setButtonSubmit() {
        System.out.println("Sign Button Clicked");
        System.out.println("Player Name: " + textFieldPlayerName.getText());
        System.out.println("Country: " + menuButtonCountry.getText());
        System.out.println("Club Name: " + textFieldClubName.getText());
        System.out.println("Position: " + menuButtonPosition.getText());
        System.out.println("Weekly Salary: " + textFieldWeeklySalary.getText());
        System.out.println("Age: " + textFieldAge.getText());
        System.out.println("Height: " + textFieldHeight.getText());
        System.out.println("Jersy Number: " + textFieldJersyNumber.getText());
        if (textFieldPlayerName.getText().isEmpty() || menuButtonCountry.getText().equals("Select One") || textFieldClubName.getText().isEmpty() || menuButtonPosition.getText().equals("Select One") || textFieldWeeklySalary.getText().isEmpty() || textFieldAge.getText().isEmpty() || textFieldHeight.getText().isEmpty()) {
            System.out.println("Please fill all the fields");
            labelWarning.setText("*Please fill all the fields!");
            labelWarning.setStyle("-fx-text-fill: red");
        }
        // Check if the weekly salary, age, height, and jersy number are numbers
        else if (!textFieldWeeklySalary.getText().matches("[0-9]+") || !textFieldAge.getText().matches("[0-9]+") || !textFieldHeight.getText().matches("[0-9]+(\\.[0-9]+)?") || !textFieldJersyNumber.getText().matches("[0-9]+")) {
            System.out.println("Please enter valid numbers");
            labelWarning.setText("*Please enter valid numbers!");
            labelWarning.setStyle("-fx-text-fill: red");
        } else {
            labelWarning.setText("");
            String playerName = textFieldPlayerName.getText();
            String country = menuButtonCountry.getText();
            String clubName = textFieldClubName.getText();
            String position = menuButtonPosition.getText();
            String weeklySalary = textFieldWeeklySalary.getText();
            String age = textFieldAge.getText();
            String height = textFieldHeight.getText();
            String jersyNumber;
            if (textFieldJersyNumber.getText().isEmpty())
                jersyNumber = "0";
            else
                jersyNumber = textFieldJersyNumber.getText();
            client.sendMessage("signplayer," + playerName + "," + country + "," + clubName + "," + position + "," + weeklySalary + "," + age + "," + height + "," + jersyNumber);


        }
    }

    public void setClient(Client client) {
        this.client = client;
        client.clearListeners();
        client.addListener(this);
    }
    public void setCurrentTeamname(String currentTeamname) {
        this.currentTeamname = currentTeamname;
        starter(currentTeamname);
    }
    public void starter(String username) {
        currentClub = new DatabaseClub().getTeam(username);
        setTeamDetails(currentClub.getTeamName());
        labelMyClub.setText(currentTeamname);
        textFieldClubName.setText(currentClub.getTeamName());
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Get Net Worth and Balance of " + currentTeamname);
        }, 0, 2, TimeUnit.SECONDS);
    }
    public void setTeamDetails(String teamname) {
        String color1 = currentClub.getColor1();
        String color2 = currentClub.getColor2();
        String logo = currentClub.getLogoPath();
        labelClubName.setText(teamname);
        anchorBackground.setStyle("-fx-background-color: " + color1 + "; -fx-background-radius: 0 0 1000 1000;");
        anchorBackground1.setStyle("-fx-background-color: " + color2 + ";");
        imageLogo.setImage(new Image(logo));

    }

    @FXML
    public void initialize() {
        setMenuButtonCountry();
        setMenuButtonPosition();
    }

    @FXML
    private void setMenuButtonCountry() {
        miAfghanistan.setOnAction(e -> menuButtonCountry.setText("Afghanistan"));
        miAustralia.setOnAction(e -> menuButtonCountry.setText("Australia"));
        miBangladesh.setOnAction(e -> menuButtonCountry.setText("Bangladesh"));
        miEngland.setOnAction(e -> menuButtonCountry.setText("England"));
        miIndia.setOnAction(e -> menuButtonCountry.setText("India"));
        miNewzealand.setOnAction(e -> menuButtonCountry.setText("New Zealand"));
        miPakistan.setOnAction(e -> menuButtonCountry.setText("Pakistan"));
        miSouthafrica.setOnAction(e -> menuButtonCountry.setText("South Africa"));
        miSrilanka.setOnAction(e -> menuButtonCountry.setText("Sri Lanka"));
        miWestindies.setOnAction(e -> menuButtonCountry.setText("West Indies"));
    }
    @FXML
    private void setMenuButtonPosition() {
        miBatsman.setOnAction(e -> menuButtonPosition.setText("Batsman"));
        miBowler.setOnAction(e -> menuButtonPosition.setText("Bowler"));
        miAllrounder.setOnAction(e -> menuButtonPosition.setText("Allrounder"));
        miWicketkeeper.setOnAction(e -> menuButtonPosition.setText("Wicketkeeper"));
    }


    @Override
    public void onServerMessage(String message) {
        Platform.runLater(() ->{
            String [] parts = message.split(":");
            if(parts[0].equals("Net Worth and Balance")){
                netWorth.setText("Net Worth: " + parts[1] + "$");
                balance.setText("Balance: " + parts[2] + "$");

            }
            else if(parts[0].equals("Player Signed")){
//                labelWarning.setText("Player Signed in Successfully");
//                labelWarning.setStyle("-fx-text-fill: green");
//                PauseTransition pause = new PauseTransition(Duration.seconds(1));
//                pause.setOnFinished(event -> labelWarning.setText(""));
//                pause.play();
                textFieldPlayerName.setText("");
                menuButtonCountry.setText("Select One");
                menuButtonPosition.setText("Select One");
                textFieldWeeklySalary.setText("");
                textFieldAge.setText("");
                textFieldHeight.setText("");
                textFieldJersyNumber.setText("");
                showAlert(Alert.AlertType.INFORMATION, "Player Signed", "Player signed in successfully.");
            }
        });
    }

    @Override
    public void onServerMessage(List<Player> players) {

    }
    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
