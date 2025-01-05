package com.example.kheladhula;

public class Club {
    private String username;
    private String password;
    private String teamName;
    private String color1;
    private String color2;
    private String logoPath;
    private long netWorth;
    private long budget;

    public Club(String username, String password, String teamName, String color1,String color2, String logoPath, long netWorth, long budget) {
        this.username = username;
        this.password = password;
        this.teamName = teamName;
        this.color1 = color1;
        this.color2 = color2;
        this.logoPath = logoPath;
        this.netWorth = netWorth;
        this.budget = budget;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getTeamName() { return teamName; }
    public String getColor1() { return color1; }
    public String getColor2() { return color2; }
    public String getLogoPath() { return logoPath; }
    public long getNetWorth() { return netWorth; }
    public long getBudget() { return budget; }
    public void setPassword(String password) { this.password = password; }
    public void setNetWorth(long netWorth) { this.netWorth = netWorth; }
    public void setBudget(long budget) { this.budget = budget; }
}
