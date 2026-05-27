package com.taskmanager.network;

import java.io.*;
import java.net.*;

public class TaskClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public String sendMessage(String message) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}