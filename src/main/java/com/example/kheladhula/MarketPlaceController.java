package com.example.kheladhula;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarketPlaceController implements ServerMessageListener {
    @FXML
    AnchorPane anchorBackground;
    @FXML
    ImageView imageLogo;
    @FXML
    Label LabelClubName;
    @FXML
    AnchorPane anchorBackground1;

    @FXML
    Label labelMyClub;

    @FXML
    private Button buttonBack;

    @FXML
    private ListView<Player> playerListView;

    @FXML
    private Label netWorth;
    @FXML
    private Label balance;

    String currentTeamname;
    Club currentClub;

    private Client client;

    boolean check = false;

    private ScheduledExecutorService scheduler;

    //List<Player> marketplacePlayers;

    public void setClient(Client client) {
        this.client = client;
        this.client.clearListeners();
        this.client.addListener(this);
        check = true;
    }


    public void setCurentTeamname(String clubName) {
        this.currentTeamname = clubName;
        starter(clubName);

    }

    public void starter(String clubName) {
        currentClub = new DatabaseClub().getTeam(clubName);
        LabelClubName.setText(clubName);
        labelMyClub.setText(clubName);

        String color1 = currentClub.getColor1();
        String color2 = currentClub.getColor2();
        String logo = currentClub.getLogoPath();
        LabelClubName.setText(currentTeamname);
        anchorBackground.setStyle("-fx-background-color: " + color1 + "; -fx-background-radius: 0 0 1000 1000;");
        anchorBackground1.setStyle("-fx-background-color: " + color2 + ";");
        imageLogo.setImage(new javafx.scene.image.Image(logo));

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Give me Marketplace Players for My club" + currentTeamname);
            client.sendMessage("Get Net Worth and Balance of " + currentTeamname);
        }, 0, 2, TimeUnit.SECONDS);
    }//this function work as an initializer

    public void displayListView(List<Player> players) {
        Platform.runLater(() -> {
            // Add the list of players to the ListView
            ObservableList<Player> playerObservableList = FXCollections.observableArrayList(players);
            playerListView.setItems(playerObservableList);
            playerListView.refresh();
            // Set a custom cell factory to use the FXML for rendering
            playerListView.setCellFactory(playerListView -> new ListCell<>() {
                @Override
                protected void updateItem(Player player, boolean empty) {
                    super.updateItem(player, empty);

                    if (empty || player == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample-list.fxml"));
                            AnchorPane pane = loader.load();

                            // Set player data in the custom cell controller
                            SampleListController cellController = loader.getController();
                            cellController.setClient(client);
                            cellController.setCurrentTeamname(currentTeamname);
                            cellController.setPlayerData(player,"Marketplace");

                            setGraphic(pane);
                        } catch (IOException e) {
                            e.printStackTrace();
                            setText("Error loading player data");
                        }
                    }
                }
            });
        });
    }

    private Stage stage;
    private Scene scene;
    private Parent root;
    public void backToDashboard(javafx.event.ActionEvent event) throws IOException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

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

    @Override
    public void onServerMessage(String message) {
        Platform.runLater(() ->{
            String [] parts = message.split(":");
            if(parts[0].equals("Net Worth and Balance")){
                netWorth.setText("Net Worth: " + parts[1] + "$");
                balance.setText("Balance: " + parts[2] + "$");
            }
        });
    }

    @Override
    public void onServerMessage(List<Player> players) {
        Platform.runLater(() -> {
            // Handle the players list (update GUI or logic)
            System.out.println("Received players in MarketPlaceController: ");
             displayListView(players);
            for (Player player : players) {
                System.out.println(player.getName());
            }
        });
    }



}
