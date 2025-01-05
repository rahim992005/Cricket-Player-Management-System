package com.example.kheladhula;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;

public class SampleListController {
    @FXML
    private ImageView playerImage;
    @FXML
    private  ImageView clubLogo;
    @FXML
    private ImageView flag;

    @FXML
    private Label age;
    @FXML
    private Label height;
    @FXML
    private Label name;
    @FXML
    private Label country;
    @FXML
    private Label club;
    @FXML
    private Label position;

    private int jersyNumber;
    @FXML
    private Label weeklySalary;

    @FXML
    private Button sellButton;
    @FXML
    private Button buyButton;

    private Player player;
    private String teamname;
    private Client client; // Add a reference to the Client class
    String currentTeamname;
    String currentTeamnameForPlayer; ;

    public void setClient(Client client) {
        this.client = client;
    }
    public void setCurrentTeamname(String currentTeamnameForPlayer){
        this.currentTeamnameForPlayer = currentTeamnameForPlayer;
    }

    public String getClubLogoPath(String clubName) {
        DatabaseClub databaseClub = new DatabaseClub();
        String path = "";
        path = databaseClub.getTeam(clubName).getLogoPath();
        return path;
    }
    public String getFlagLogoPath(String country){
        country = country.replace(" ", "");
        return "/com/example/kheladhula/images/"+ country.toLowerCase() +".png";
    }
    public String getPlayerImage(String playerName) {
        String imagePath = "/com/example/kheladhula/images/" + playerName + ".png";
        try {
            if (getClass().getResource(imagePath) != null) {
                return imagePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/com/example/kheladhula/images/pp.png";
    }



    public void setPlayerData(Player player, String currentTeamname) {
        this.player = player;
        name.setText(player.getName());
        country.setText(player.getCountry());
        age.setText(String.valueOf(player.getAge()));
        height.setText(String.valueOf(player.getHeight()));
        club.setText(player.getClub());
        position.setText(player.getPosition());
        jersyNumber = player.getNumber();
        weeklySalary.setText(String.valueOf(player.getWeeklySalary()));
        flag.setImage(new javafx.scene.image.Image(getFlagLogoPath(player.getCountry())));
        clubLogo.setImage(new javafx.scene.image.Image(getClubLogoPath(player.getClub())));
        playerImage.setImage(new javafx.scene.image.Image(getPlayerImage(player.getName())));
        this.currentTeamname = currentTeamname;
        if(currentTeamname.equals("Marketplace")){
            sellButton.setDisable(true);
            buyButton.setDisable(false);
        }
        else if(player.getIsMarketplace()){
            sellButton.setDisable(true);
            buyButton.setDisable(true);
        }
        else if (player.getClub().equals(currentTeamname)) {
            sellButton.setDisable(false);
            buyButton.setDisable(true);
        }
        else {
            sellButton.setDisable(true);
            buyButton.setDisable(true);
        }
    }

    @FXML
    private void handleBuyButtonAction() {
        buyPlayer();
    }

    @FXML
    private void handleSellButtonAction() {
        sellPlayer();
    }

    private void buyPlayer() {
        System.out.println("Buy button clicked");
        buyButton.setDisable(true);
        if (player != null && client != null) {
            System.out.println("Buying player: " + player.getName() + " from " + currentTeamnameForPlayer);
            client.sendMessage("Buy Player:" + player.getName() + ":" + currentTeamnameForPlayer);
        }
    }

    private void sellPlayer() {
        sellButton.setDisable(true);
        if (player != null && client != null) {
            System.out.println("Selling player: " + player.getName() + " to Marketplace");
            client.sendMessage("Sell Player:" + player.getName() +":"+player.getClub());
        }
    }
}
