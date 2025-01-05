package com.example.kheladhula;

import java.util.List;

// Define the Listener Interface
public interface ServerMessageListener {
    void onServerMessage(String message);
    void onServerMessage(List<Player> players);
}
