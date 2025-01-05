package com.example.kheladhula;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseClub {
    private static final String FILE_PATH = "src/main/resources/com/example/kheladhula//Text File/teamdetails.txt";
    private Map<String, Club> clubs = new HashMap<>();

    DatabaseClub() {
        loadClubs();
    }

    private void loadClubs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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

    public Club getTeam(String username) {
        return clubs.get(username);
    }
    public String getLogoPatha(String username) {
        return clubs.get(username).getLogoPath();
    }
}
