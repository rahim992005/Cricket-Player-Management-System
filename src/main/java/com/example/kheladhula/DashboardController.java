package com.example.kheladhula;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardController implements ServerMessageListener {

    @FXML
    AnchorPane anchorBackground;
    @FXML
    AnchorPane anchorBackground1;
    @FXML
    ImageView imageLogo;
    @FXML
    Label LabelClubName;

    @FXML
    Label labelMyClub;

    @FXML
    private Button buttonMyPlayers;
    @FXML
    private  MenuButton buttonOtherClubs;
    @FXML
    private Button buttonMarketplace;

    @FXML
    private MenuItem menuItemLogout;
    @FXML
    private MenuItem menuItemChangePassword;
    @FXML
    private MenuItem myProfileMyClub;

    @FXML
    private CheckMenuItem cmAfghanistan;
    @FXML
    private CheckMenuItem cmAustralia;
    @FXML
    private CheckMenuItem cmBangladesh;
    @FXML
    private CheckMenuItem cmEngland;
    @FXML
    private CheckMenuItem cmIndia;
    @FXML
    private CheckMenuItem cmNewZealand;
    @FXML
    private CheckMenuItem cmPakistan;
    @FXML
    private CheckMenuItem cmSouthAfrica;
    @FXML
    private CheckMenuItem cmSriLanka;
    @FXML
    private CheckMenuItem cmWestIndies;
    @FXML
    private CheckMenuItem cmAnyCountry;

    @FXML
    private CheckMenuItem cmBatsman;
    @FXML
    private CheckMenuItem cmBowler;
    @FXML
    private CheckMenuItem cmAllRounder;
    @FXML
    private CheckMenuItem cmWicketKeeper;
    @FXML
    private CheckMenuItem cmAnyPosition;

    @FXML
    private MenuItem highestSalary;
    @FXML
    private MenuItem maxAge;
    @FXML
    private MenuItem maxHeight;

    @FXML
    private TextField minSalary;
    @FXML
    private TextField maxSalary;
    @FXML
    private TextField searchPlayer;

    @FXML
    private MenuItem kkr;
    @FXML
    private MenuItem rcb;
    @FXML
    private MenuItem rr;
    @FXML
    private MenuItem csk;
    @FXML
    private MenuItem dc;
    @FXML
    private MenuItem mi;
    @FXML
    private MenuItem lsg;
    @FXML
    private MenuItem pk;
    @FXML
    private MenuItem srh;
    @FXML
    private MenuItem gt;

    @FXML
    Label netWorth;
    @FXML
    Label balance;

    @FXML
    private ListView<Player> playerListView;

    private String currentTeamname; // login teamname
    private Club currentClub; // login club
    private String Teamname; // selected teamname

    private double netWorthValue;
    private double balanceValue;

    private Client client ;

    boolean check = true;

    private ScheduledExecutorService scheduler;

    String[] countryWiseFilter = new String[11];
    String[] positionWiseFilter = new String[5];
    int minimumSalary = 0;
    int maximumSalary = 1000000000;

    public void setClient(Client client) {
        this.client = client;
        this.client.clearListeners();
        this.client.addListener(this);
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
            System.out.println("Received players in DashboardController: ");
            //print players name
            displayListView(players);
//            for (Player player : players) {
//                System.out.println(player.getName()+ " " + player.getClub());
//            }
        });
    }

    public void setCurentTeamname(String username) {
        this.currentTeamname = username;
        starter(username);
    }

    public void starter(String username){
        getClubDetails();
        currentClub = new DatabaseClub().getTeam(username);
        setTeamDetails(currentClub.getTeamName());
        labelMyClub.setText(currentTeamname);
    }//this function work as an initializer

    public void setButtonMyPlayers(){
        setTeamDetails(currentTeamname);
    }

    public void getClubDetails() {
        kkr.setDisable(currentTeamname.equals("Kolkata Knight Riders"));
        rcb.setDisable(currentTeamname.equals("Royal Challengers Bangalore"));
        rr.setDisable(currentTeamname.equals("Rajasthan Royals"));
        csk.setDisable(currentTeamname.equals("Chennai Super Kings"));
        dc.setDisable(currentTeamname.equals("Delhi Capitals"));
        mi.setDisable(currentTeamname.equals("Mumbai Indians"));
        lsg.setDisable(currentTeamname.equals("Lucknow Super Giants"));
        pk.setDisable(currentTeamname.equals("Punjab Kings"));
        srh.setDisable(currentTeamname.equals("Sunrisers Hyderabad"));
        gt.setDisable(currentTeamname.equals("Gujarat Titans"));

        kkr.setOnAction(event -> setTeamDetails("Kolkata Knight Riders"));
        rcb.setOnAction(event -> setTeamDetails("Royal Challengers Bangalore"));
        rr.setOnAction(event -> setTeamDetails("Rajasthan Royals"));
        csk.setOnAction(event -> setTeamDetails("Chennai Super Kings"));
        dc.setOnAction(event -> setTeamDetails("Delhi Capitals"));
        mi.setOnAction(event -> setTeamDetails("Mumbai Indians"));
        lsg.setOnAction(event -> setTeamDetails("Lucknow Super Giants"));
        pk.setOnAction(event -> setTeamDetails("Punjab Kings"));
        srh.setOnAction(event -> setTeamDetails("Sunrisers Hyderabad"));
        gt.setOnAction(event -> setTeamDetails("Gujarat Titans"));
    }

    public void setTeamDetails(String teamname) {
        Teamname = teamname;
        Club tempClub = new DatabaseClub().getTeam(teamname);
        String color1 = tempClub.getColor1();
        String color2 = tempClub.getColor2();
        String logo = tempClub.getLogoPath();
        LabelClubName.setText(teamname);
        anchorBackground.setStyle("-fx-background-color: " + color1 + "; -fx-background-radius: 0 0 1000 1000;");
        anchorBackground1.setStyle("-fx-background-color: " + color2 + ";");
        imageLogo.setImage(new Image(logo));

        schedulePlayerUpdates();
    }

    private void schedulePlayerUpdates() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Give me All Players of " + Teamname);
            client.sendMessage("Get Net Worth and Balance of " + Teamname);
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void scheduleSearchPlayerUpdates(String search) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Give me Players" + search + ":" + Teamname);
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void scheduleMaxAgePlayerUpdates() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Give me Max Age Players" + Teamname);
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void scheduleMaxHeightPlayerUpdates() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Give me Max Height Players" + Teamname);
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void scheduleMaxSalaryPlauerUpdates() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage("Give me Max Weekly Salary Players" + Teamname);
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void getCountryWiseFilter() {
        if (cmAfghanistan.isSelected()) {
            countryWiseFilter[0] = "Afghanistan";
        } else {
            countryWiseFilter[0] = "";
        }
        if (cmAustralia.isSelected()) {
            countryWiseFilter[1] = "Australia";
        } else {
            countryWiseFilter[1] = "";
        }
        if (cmBangladesh.isSelected()) {
            countryWiseFilter[2] = "Bangladesh";
        } else {
            countryWiseFilter[2] = "";
        }
        if (cmEngland.isSelected()) {
            countryWiseFilter[3] = "England";
        } else {
            countryWiseFilter[3] = "";
        }
        if (cmIndia.isSelected()) {
            countryWiseFilter[4] = "India";
        } else {
            countryWiseFilter[4] = "";
        }
        if (cmNewZealand.isSelected()) {
            countryWiseFilter[5] = "New Zealand";
        } else {
            countryWiseFilter[5] = "";
        }
        if (cmPakistan.isSelected()) {
            countryWiseFilter[6] = "Pakistan";
        } else {
            countryWiseFilter[6] = "";
        }
        if (cmSouthAfrica.isSelected()) {
            countryWiseFilter[7] = "South Africa";
        } else {
            countryWiseFilter[7] = "";
        }
        if (cmSriLanka.isSelected()) {
            countryWiseFilter[8] = "Sri Lanka";
        } else {
            countryWiseFilter[8] = "";
        }
        if (cmWestIndies.isSelected()) {
            countryWiseFilter[9] = "West Indies";
        } else {
            countryWiseFilter[9] = "";
        }
    }

    public void getPositionWiseFilter() {
        if (cmBatsman.isSelected()) {
            positionWiseFilter[0] = "Batsman";
        } else {
            positionWiseFilter[0] = "";
        }
        if (cmBowler.isSelected()) {
            positionWiseFilter[1] = "Bowler";
        } else {
            positionWiseFilter[1] = "";
        }
        if (cmAllRounder.isSelected()) {
            positionWiseFilter[2] = "Allrounder";
        } else {
            positionWiseFilter[2] = "";
        }
        if (cmWicketKeeper.isSelected()) {
            positionWiseFilter[3] = "Wicketkeeper";
        } else {
            positionWiseFilter[3] = "";
        }
    }

    public void getSalaryWiseFilter() {
        try {
            int minSalaryValue = Integer.parseInt(minSalary.getText());
            if (minSalaryValue > 0) {
                minimumSalary = minSalaryValue;
            } else {
                minimumSalary = 0;
                minSalary.setText("");
            }
        } catch (NumberFormatException e) {
            minimumSalary = 0;
            minSalary.setText("");
        }

        try {
            int maxSalaryValue = Integer.parseInt(maxSalary.getText());
            if (maxSalaryValue > 0) {
                maximumSalary = maxSalaryValue;
            } else {
                maximumSalary = 1000000000;
                maxSalary.setText("");
            }
        } catch (NumberFormatException e) {
            maximumSalary = 1000000000;
            maxSalary.setText("");
        }
    }

    public void applyFilters() {
        getCountryWiseFilter();
        getPositionWiseFilter();
        getSalaryWiseFilter();
        schedulePlayerUpdates();
    }

    public void getMaxAge(){
        scheduleMaxAgePlayerUpdates();
    }
    public void getMaxHeight(){
        scheduleMaxHeightPlayerUpdates();
    }
    public void getHighestSalaryPlayer(){
        scheduleMaxSalaryPlauerUpdates();
    }

    private List<Player> filterPlayers(List<Player> players) {
        List<Player> playerList = new ArrayList<>();

        if (players != null) {
            for (Player player : players) {
                boolean matchesCountry = cmAnyCountry.isSelected() ||
                        player.getCountry().equals(countryWiseFilter[0]) ||
                        player.getCountry().equals(countryWiseFilter[1]) ||
                        player.getCountry().equals(countryWiseFilter[2]) ||
                        player.getCountry().equals(countryWiseFilter[3]) ||
                        player.getCountry().equals(countryWiseFilter[4]) ||
                        player.getCountry().equals(countryWiseFilter[5]) ||
                        player.getCountry().equals(countryWiseFilter[6]) ||
                        player.getCountry().equals(countryWiseFilter[7]) ||
                        player.getCountry().equals(countryWiseFilter[8]) ||
                        player.getCountry().equals(countryWiseFilter[9]);

                boolean matchesPosition = cmAnyPosition.isSelected() ||
                        player.getPosition().equals(positionWiseFilter[0]) ||
                        player.getPosition().equals(positionWiseFilter[1]) ||
                        player.getPosition().equals(positionWiseFilter[2]) ||
                        player.getPosition().equals(positionWiseFilter[3]);

                boolean matchesSalary = player.getWeeklySalary() >= minimumSalary && player.getWeeklySalary() <= maximumSalary;

                if (matchesCountry && matchesPosition && matchesSalary) {
                    playerList.add(player);
                }
            }
        } else {
            System.out.println("No players found for team: " + Teamname);
        }

        return playerList;
    }

    public void getsearchPlayer() {
        String search = searchPlayer.getText();
        if(search.isEmpty()){
            schedulePlayerUpdates();
        }else{
            cmAnyCountry.setSelected(true);
            cmAnyPosition.setSelected(true);
            minSalary.setText("");
            minimumSalary = 0;
            maxSalary.setText("");
            maximumSalary = 1000000000;
            scheduleSearchPlayerUpdates(search);
            searchPlayer.setText("");
        }
    }

    public void displayListView(List<Player> players) {
        Platform.runLater(() -> {
            ObservableList<Player> playerObservableList = FXCollections.observableArrayList();
            playerObservableList.addAll(filterPlayers(players));
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
                            cellController.setPlayerData(player,currentTeamname);

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
    public void switchToMarketplace(ActionEvent event) throws IOException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("market-place.fxml"));
        Parent root = loader.load();

        MarketPlaceController marketPlaceController = loader.getController();
        marketPlaceController.setClient(client);
        marketPlaceController.setCurentTeamname(currentTeamname);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onChangePassword(ActionEvent event) {
        if(scheduler != null && !scheduler.isShutdown()){
            scheduler.shutdown();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("change-password.fxml"));
            Parent root = loader.load();

            ChangePasswordController changePasswordController = loader.getController();
            changePasswordController.setClient(client);
            changePasswordController.setCurrentTeamname(currentTeamname);

            // Assuming you have a reference to a Node in your controller, e.g., anchorBackground
            stage = (Stage) anchorBackground.getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Change Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onLogout() {
        try {
            //stop the scheduler
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
            client.stopClient();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-screen.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) menuItemLogout.getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading in login screen FXML file: " + e.getMessage());
        }
    }
    @FXML
    private void switchToSigningPlayer(ActionEvent event) throws IOException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("signing-player.fxml"));
        Parent root = loader.load();

        SigningPlayerController signingPlayerController = loader.getController();
        signingPlayerController.setClient(client);
        signingPlayerController.setCurrentTeamname(currentTeamname);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
