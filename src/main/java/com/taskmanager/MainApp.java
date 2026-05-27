package com.taskmanager;

import com.taskmanager.network.TaskServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private TaskServer server = new TaskServer();
    private Thread serverThread = new Thread(server);

    @Override
    public void start(Stage stage) throws Exception {
        // بنشغّل الـ Server في background thread
        serverThread.setDaemon(true);
        serverThread.start();

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/taskview.fxml"));
        Scene scene = new Scene(loader.load(), 700, 500);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        stage.setTitle("Smart Task Manager");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        server.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}