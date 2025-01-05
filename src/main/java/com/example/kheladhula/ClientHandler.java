package com.example.kheladhula;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static com.example.kheladhula.Server.marketplace;

class ClientHandler extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clubName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            clubName = (String) in.readObject();
            System.out.println("Club connected: " + clubName);
            synchronized (Server.clients) {
                Server.clients.put(clubName, new PrintWriter(new OutputStreamWriter(out), true));
            }

            String message;
            while ((message = (String) in.readObject()) != null) {
                System.out.println("Received message from " + clubName + ": " + message);
                processMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            synchronized (Server.clients) {
                Server.clients.remove(clubName);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(clubName + " disconnected.");
        }
    }

    private void cleanupResources() {
        synchronized (Server.clients) {
            if (clubName != null) {
                Server.clients.remove(clubName);
            }
        }
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Player> getAllMarketplacePlayers() {
        List<Player> allMarketplacePlayers = new ArrayList<>();
        synchronized (marketplace) {
            for (List<Player> players : marketplace.values()) {
                allMarketplacePlayers.addAll(players);
            }
        }
        return allMarketplacePlayers;
    }

    public Player findPlayerByName(String playerName) {
        for (Player player : Server.allPlayers) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    private void processMessage(String message) {
        if (message.startsWith("Give me All Players of ")) {
            String clubName = message.substring("Give me All Players of ".length());
            List<Player> players;
            synchronized (Server.clubwisePlayers) {
                players = new ArrayList<>(Server.clubwisePlayers.getOrDefault(clubName, Collections.emptyList()));
            }
            try {
//                for (Player player : players) {
//                    System.out.println(player.getName() + player.getClub());
//                }
                out.writeObject(players);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (message.startsWith("Give me Marketplace Players for My club")) {
            String clubName = message.substring("Give me Marketplace Players for My club".length());
            List<Player> players;
            synchronized (marketplace) {
                players = new ArrayList<>(marketplace.getOrDefault(clubName, Collections.emptyList()));
            }
            try {
                out.writeObject(players);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(message.startsWith("Give me Players")){
            String searchandClub = message.substring("Give me Players".length());
            String[] parts = searchandClub.split(":");
            String search = parts[0];
            String clubName = parts[1];
            List<Player> players = Server.clubwisePlayers.get(clubName);
            //if search contains in the clubwisePlayers then send the players
            List<Player> searchPlayers = new ArrayList<>();
            for (Player player : players) {
                if(player.getName().toLowerCase().contains(search.toLowerCase())){
                    searchPlayers.add(player);
                }
            }
            try {
                out.writeObject(searchPlayers);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(message.startsWith("Give me Max Age Players")){
            String clubName = message.substring("Give me Max Age Players".length());
            List<Player> players = Server.clubwisePlayers.get(clubName);
            //if search contains in the clubwisePlayers then send the players
            List<Player> searchPlayers = new ArrayList<>();
            int maxAge = 0;
            for (Player player : players) {
                if(player.getAge() > maxAge){
                    maxAge = player.getAge();
                }
            }
            for (Player player : players) {
                if(player.getAge() == maxAge){
                    searchPlayers.add(player);
                }
            }
            try {
                out.writeObject(searchPlayers);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(message.startsWith("Give me Max Height Players")){
            String clubName = message.substring("Give me Max Height Players".length());
            List<Player> players = Server.clubwisePlayers.get(clubName);
            //if search contains in the clubwisePlayers then send the players
            List<Player> searchPlayers = new ArrayList<>();
            double maxHeight = 0;
            for (Player player : players) {
                if(player.getHeight() > maxHeight){
                    maxHeight = player.getHeight();
                }
            }
            for (Player player : players) {
                if(player.getHeight() == maxHeight){
                    searchPlayers.add(player);
                }
            }
            try {
                out.writeObject(searchPlayers);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(message.startsWith("Give me Max Weekly Salary Players")){
            String clubName = message.substring("Give me Max Weekly Salary Players".length());
            List<Player> players = Server.clubwisePlayers.get(clubName);
            //if search contains in the clubwisePlayers then send the players
            List<Player> searchPlayers = new ArrayList<>();
            int maxWeeklySalary = 0;
            for (Player player : players) {
                if(player.getWeeklySalary() > maxWeeklySalary){
                    maxWeeklySalary = player.getWeeklySalary();
                }
            }
            for (Player player : players) {
                if(player.getWeeklySalary() == maxWeeklySalary){
                    searchPlayers.add(player);
                }
            }
            try {
                out.writeObject(searchPlayers);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (message.startsWith("Sell Player")) {
            String[] parts = message.split(":");
            String playerName = parts[1];
            String sellerClub = parts[2];
            addToMarketplace(sellerClub, findPlayerByName(playerName));
            //isMarketPlace true set for allplayers and clubwiseplayers in server permanently

        }
        else if (message.startsWith("Buy Player")) {
            String[] parts = message.split(":");
            String playerName = parts[1];
            String buyerClub = parts[2];

            removeFromMarketplace(playerName, buyerClub);
            //update the networth and balance of the clubs
            synchronized (Server.clubs) {
                Club buyer = Server.clubs.get(buyerClub);
                Club seller = Server.clubs.get(findPlayerByName(playerName).getClub());
                buyer.setBudget(buyer.getBudget() - findPlayerByName(playerName).getWeeklySalary());
                seller.setBudget(seller.getBudget() + findPlayerByName(playerName).getWeeklySalary());
                buyer.setNetWorth(buyer.getNetWorth() + findPlayerByName(playerName).getWeeklySalary());
                seller.setNetWorth(seller.getNetWorth() - findPlayerByName(playerName).getWeeklySalary());
            }

        }
        // existing code
        else if (message.startsWith("LOGIN")) {
            String[] parts = message.split(":");
            String username = parts[1];
            String password = parts[2];

            // Validate the username and password (this is just a placeholder, replace with actual validation logic)
            boolean isValidLogin = validateLogin(username, password);

            try {
                if (isValidLogin) {
                    System.out.println("Login successful for " + username);
                    out.writeObject("LOGIN_SUCCESS");
                } else {
                    System.out.println("Login failed for " + username);
                 out.writeObject("LOGIN_FAILURE");
                }
             out.flush();
            } catch (IOException e) {
             e.printStackTrace();
            }
        }
        else if(message.startsWith("changepassword")){
            String[] parts = message.split(":");
            String username = parts[1];
            String oldPassword = parts[2];
            String newPassword = parts[3];
            //check old password and username. and confirm it to the client . then change the password
            boolean isValidLogin = validateLogin(username, oldPassword);
            try {
                if (isValidLogin) {
                    synchronized (Server.clubs) {
                        if (Server.clubs.containsKey(username) && Server.clubs.get(username).getPassword().equals(oldPassword)) {
                            Server.clubs.get(username).setPassword(newPassword);
                            System.out.println("Password changed successfully for " + username);
                        }
                    }
                    out.writeObject("Password_change_successful");
                } else {
                    System.out.println("Password change failed for " + username);
                    out.writeObject("Password_change_failed");
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(message.startsWith("signplayer,")){
            String[] parts = message.split(",");
            String playerName = parts[1];
            String country = parts[2];
            String clubName = parts[3];
            String position = parts[4];
            int weeklySalary = Integer.parseInt(parts[5]);
            int age = Integer.parseInt(parts[6]);
            double height = Double.parseDouble(parts[7]);
            int jersyNumber = Integer.parseInt(parts[8]);
            Player player = new Player(playerName, country, age, height, clubName, position, jersyNumber, weeklySalary,false);
            Server.allPlayers.add(player);
            synchronized (Server.clubwisePlayers) {
                if (!Server.clubwisePlayers.containsKey(clubName)) {
                    Server.clubwisePlayers.put(clubName, new ArrayList<>());
                }
                Server.clubwisePlayers.get(clubName).add(player);
            }
            //update the networth and balance of the clubs
            synchronized (Server.clubs) {
                Club club = Server.clubs.get(clubName);
                club.setBudget(club.getBudget() - weeklySalary);
                club.setNetWorth(club.getNetWorth() + weeklySalary);
            }
            try {
                out.writeObject("Player Signed");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (message.startsWith("Get Net Worth and Balance of ")) {
            String clubName = message.substring("Get Net Worth and Balance of ".length());
            Club club = Server.clubs.get(clubName);
            String response = "Net Worth and Balance:"+ club.getNetWorth() + ":" + club.getBudget();
            try {
                out.writeObject(response);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private boolean validateLogin(String username, String password) {
        synchronized (Server.clubs) {
           // System.out.println(Server.clubs.containsKey(username) + " " + Server.clubs.get(username).getUsername() + " " + Server.clubs.get(username).getPassword());
            return Server.clubs.containsKey(username) && Server.clubs.get(username).getPassword().equals(password);
        }
    }

    private void addToMarketplace(String sellerClub, Player player) {
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }
        // Update the player in Server.allPlayers
        for (Player p : Server.allPlayers) {
            if (p.getName().equals(player.getName())) {
                p.setIsMarketPlace(true);
                break;
            }
        }

        // Update the player in Server.clubwisePlayers
        synchronized (Server.clubwisePlayers) {
            for (Player p : Server.clubwisePlayers.get(sellerClub)) {
                if (p.getName().equals(player.getName())) {
                    Player player1 = new Player(player.getName(), p.getCountry(), p.getAge(), p.getHeight(), p.getClub(), p.getPosition(), p.getNumber(), p.getWeeklySalary(),true);
                    //p.setIsMarketPlace(true);
                    Server.clubwisePlayers.get(sellerClub).remove(p);
                    Server.clubwisePlayers.get(sellerClub).add(player1);
                    break;
                }
            }
        }

        // Add the player to the marketplace
        synchronized (marketplace) {
            for (String club : marketplace.keySet()) {
                if (!club.equals(sellerClub)) { // Add only to other clubs
                    List<Player> clubMarketplace = marketplace.get(club);
                    if (!clubMarketplace.contains(player)) { // Avoid duplicates
                        clubMarketplace.add(player);
                        System.out.println("Added player to marketplace for club: " + club);
                    }
                }
            }
        }
    }

    private void removeFromMarketplace(String playerName, String buyerClub) {
        synchronized (marketplace) {
            for (String club : marketplace.keySet()) {
                marketplace.get(club).removeIf(player -> player.getName().equals(playerName));
            }
        }

        Player player = findPlayerByName(playerName);
        String sellerClub = player.getClub();
        for (Player p : Server.clubwisePlayers.get(sellerClub)) {
            if (p.getName().equals(playerName)) {
                Player player1 = new Player(playerName, p.getCountry(), p.getAge(), p.getHeight(),buyerClub, p.getPosition(), p.getNumber(), p.getWeeklySalary(),false);
                //p.setIsMarketPlace(true);
                Server.clubwisePlayers.get(sellerClub).remove(p);
                Server.clubwisePlayers.get(buyerClub).add(player1);
                break;
            }
        }

    }
}