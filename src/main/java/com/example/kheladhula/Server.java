package com.example.kheladhula;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final String FILE_PATH = "src/main/resources/com/example/kheladhula/Text File/players.txt";
    private static final String FILE_PATH2 = "src/main/resources/com/example/kheladhula//Text File/teamdetails.txt";
    private static final String FILE_PATH3 = "src/main/resources/com/example/kheladhula//Text File/marketplace.txt";

    public static Map<String, List<Player>> marketplace = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, PrintWriter> clients = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, List<Player>> clubwisePlayers = new HashMap<>();
    public static List<Player> allPlayers = new ArrayList<>();
    public static Map<String, Club> clubs = new HashMap<>();
    private static final int PORT = 12345;

    public static void main(String[] args) {

        Server server = new Server();
        server.loadPlayersFromFile(FILE_PATH);
        server.loadClubs(FILE_PATH2);
        server.initializeMarketplace();
        server.loadMarketplacePlayers();

        //server.temporaryMarketPlacePlayers();
        // Add shutdown hook to save club and player details on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveClubDetailsToFile();
            savePlayerDetailsToFile();
            saveMarketplaceToFile();
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadPlayersFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[6].isEmpty()) {
                    parts[6] = "-1";
                }
                String name = parts[0].trim();
                String country = parts[1].trim();
                int age = Integer.parseInt(parts[2]);
                double height = Double.parseDouble(parts[3]);
                String club = parts[4].trim();
                String position = parts[5].trim();
                int number = Integer.parseInt(parts[6]);
                int weeklySalary = Integer.parseInt(parts[7]);
                boolean isMarketplace = Boolean.parseBoolean(parts[8]);
                Player player = new Player(name, country, age, height, club, position, number, weeklySalary,isMarketplace);
                clubwisePlayers.computeIfAbsent(club, k -> new ArrayList<>()).add(player);
                allPlayers.add(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    //load players from the file to the marketplace
    // Load players from the file to the marketplace
    private void loadMarketplacePlayers() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH3))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String club = parts[0].trim();
                String name = parts[1].trim();
                String country = parts[2].trim();
                int age = Integer.parseInt(parts[3]);
                double height = Double.parseDouble(parts[4]);
                String playerClub = parts[5].trim();
                String position = parts[6].trim();
                int number = Integer.parseInt(parts[7]);
                int weeklySalary = Integer.parseInt(parts[8]);
                boolean isMarketplace = Boolean.parseBoolean(parts[9]);
                Player player = new Player(name, country, age, height, playerClub, position, number, weeklySalary, isMarketplace);
                marketplace.computeIfAbsent(club, k -> new ArrayList<>()).add(player);
            }
        } catch (IOException e) {
            System.err.println("Error loading marketplace: " + e.getMessage());
        }
    }

    //marketplace initialization with all 10 club with 0 players
    private void initializeMarketplace() {
        for (String club : clubwisePlayers.keySet()) {
            marketplace.put(club, new ArrayList<>());
        }
    }

    private void loadClubs(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String username = data[0];
                String password = data[1];
                String teamName = data[2];
                String color1 = data[3];
                String color2 = data[4];
                String logoPath = data[5];
                long netWorth = Long.parseLong(data[6]);
                long budget = Long.parseLong(data[7]);

                Club team = new Club(username, password, teamName, color1,color2, logoPath, netWorth, budget);
                clubs.put(username, team); // Map username to Team
            }
        } catch (IOException e) {
            System.err.println("Error loading teams: " + e.getMessage());
        }
    }

    // Save the marketplace to a file
    public static void saveMarketplaceToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH3))) {
            for (Map.Entry<String, List<Player>> entry : marketplace.entrySet()) {
                String club = entry.getKey();
                for (Player player : entry.getValue()) {
                    writer.println(club + "," + player.getName() + "," + player.getCountry() + "," + player.getAge() + "," + player.getHeight() + "," + player.getClub() + "," + player.getPosition() + "," + player.getNumber() + "," + player.getWeeklySalary() + "," + player.getIsMarketplace());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving marketplace: " + e.getMessage());
        }
    }

    public static void saveClubDetailsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH2))) {
            for (Club club : clubs.values()) {
                writer.println(club.getUsername() + "," + club.getPassword() + "," + club.getTeamName() + "," + club.getColor1() + "," + club.getColor2() + "," + club.getLogoPath() + "," + club.getNetWorth() + "," + club.getBudget());
            }
        } catch (IOException e) {
            System.err.println("Error saving club details: " + e.getMessage());
        }
    }
    //save the player details to the file use clubwisePlayers map
    public static void savePlayerDetailsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (List<Player> players : clubwisePlayers.values()) {
                for (Player player : players) {
                    writer.println(player.getName() + "," + player.getCountry() + "," + player.getAge() + "," + player.getHeight() + "," + player.getClub() + "," + player.getPosition() + "," + player.getNumber() + "," + player.getWeeklySalary() + "," + player.getIsMarketplace());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving player details: " + e.getMessage());
        }
    }

}