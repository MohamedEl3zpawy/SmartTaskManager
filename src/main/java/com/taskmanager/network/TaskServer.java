package com.taskmanager.network;

import java.io.*;
import java.net.*;

public class TaskServer implements Runnable {
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private boolean running = false;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Server started on port " + PORT);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    out.println("Server received: " + message);
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            if (running) e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}